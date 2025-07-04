<template>
  <el-container class="chat-container">
    <el-main class="chat-messages" ref="messagesContainer">
      <div v-for="(item, index) in messages" :key="index" :class="['message-item', `message-item-${item.role}`]">
        <el-avatar :icon="item.role === 'user' ? 'User' : 'ChatDotRound'" class="message-avatar" :style="{ backgroundColor: item.role === 'user' ? '#409eff' : '#67c23a' }" />
        <div class="message-content">
          <div class="message-role">{{ item.role === 'user' ? 'You' : 'Advisor' }}</div>
          <el-card shadow="hover" class="message-card">
            <div v-if="item.content" v-html="renderMarkdown(item.content)"></div>
            <div v-else class="typing-indicator">
              <span></span><span></span><span></span>
            </div>
          </el-card>
        </div>
      </div>
    </el-main>
    <el-footer class="chat-input-container">
      <el-input
        v-model="newMessage"
        placeholder="Ask about funds, strategies, or market analysis..."
        @keyup.enter="sendMessage"
        :disabled="isLoading"
        clearable
        size="large"
      >
        <template #append>
          <el-button type="primary" @click="sendMessage" :loading="isLoading" :disabled="!newMessage.trim()">
            Send
          </el-button>
        </template>
      </el-input>
    </el-footer>
  </el-container>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue';
import { fetchChatStream } from '@/api/chat';
import { ElMessage } from 'element-plus';
import MarkdownIt from 'markdown-it';

const md = new MarkdownIt();
const messages = ref([]);
const newMessage = ref('');
const isLoading = ref(false);
const messagesContainer = ref(null);

const renderMarkdown = (content) => {
    return md.render(content || '');
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    }
  });
};

onMounted(() => {
    // messages.value.push({
    //     role: 'assistant',
    //     content: '你好！我是你的智能金融投顾助手。你需要什么帮助？'
    // });
});

const sendMessage = () => {
  const content = newMessage.value.trim();
  if (!content || isLoading.value) return;

  messages.value.push({ role: 'user', content });
  newMessage.value = '';
  isLoading.value = true;
  scrollToBottom();

  messages.value.push({ role: 'assistant', content: '' });

  const chatHistory = messages.value.slice(0, -1).map(m => ({ role: m.role, content: m.content }));

  fetchChatStream(
    chatHistory,
    (response) => {
      const lastMessage = messages.value[messages.value.length - 1];
      if (lastMessage && lastMessage.role === 'assistant') {
        lastMessage.content += response.content;
        scrollToBottom();
      }
    },
    (error) => {
      console.error('SSE Error:', error);
      ElMessage.error('An error occurred while communicating with the advisor.');
      isLoading.value = false;
      messages.value.pop();
    },
    () => {
      isLoading.value = false;
      scrollToBottom();
    }
  );
};
</script>

<style scoped>
.chat-container {
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
}

.chat-messages {
  flex-grow: 1;
  overflow-y: auto;
  padding: 20px;
}

.message-item {
  display: flex;
  margin-bottom: 20px;
}

.message-item-user {
  flex-direction: row-reverse;
}

.message-content {
  max-width: 70%;
}

.message-item-user .message-content {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.message-avatar {
  margin: 0 10px;
}

.message-role {
  font-size: 12px;
  color: #909399;
  margin-bottom: 5px;
}

.message-card {
  border-radius: 10px;
}

.message-item-user .message-card {
    background-color: #ecf5ff;
}

.chat-input-container {
  padding: 10px 20px;
  background-color: #ffffff;
  border-top: 1px solid #dcdfe6;
}

.typing-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px 0;
}

.typing-indicator span {
  height: 8px;
  width: 8px;
  background-color: #909399;
  border-radius: 50%;
  display: inline-block;
  margin: 0 2px;
  animation: bounce 1.4s infinite ease-in-out both;
}

.typing-indicator span:nth-of-type(1) {
  animation-delay: -0.32s;
}

.typing-indicator span:nth-of-type(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1.0);
  }
}
</style>
