import request from '@/utils/request'

export function fetchChatStream(messages, onMessage, onError, onComplete) {
  const token = localStorage.getItem('token');

  const payload = {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({ messages }),
  };

  fetch('/api/chat', payload)
    .then(response => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const reader = response.body.getReader();
      const decoder = new TextDecoder('utf-8');

      function read() {
        reader.read().then(({ done, value }) => {
          if (done) {
            if(onComplete) onComplete();
            return;
          }
          const chunk = decoder.decode(value, { stream: true });
          // SSE events are separated by double newlines, and each event is prefixed with "data: "
          const lines = chunk.split('\n');
          lines.forEach(line => {
            if (line.startsWith('data:')) {
              try {
                const data = line.substring(5).trim();
                if (data) {
                    const parsed = JSON.parse(data);
                    onMessage(parsed);
                }
              } catch (e) {
                 console.error("Failed to parse SSE data chunk:", line, e);
                 if (onError) onError(new Error("Failed to parse server event."));
              }
            } else if (line.startsWith('error:')) {
                if (onError) onError(new Error(line.substring(6).trim()));
            }
          });
          read();
        }).catch(err => {
            if(onError) onError(err);
        });
      }
      read();
    })
    .catch(err => {
        if(onError) onError(err);
    });
} 