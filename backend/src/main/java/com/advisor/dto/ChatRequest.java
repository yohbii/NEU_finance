package com.advisor.dto;

import java.util.List;

public class ChatRequest {

    private List<ChatDTO> messages;

    public List<ChatDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatDTO> messages) {
        this.messages = messages;
    }
} 