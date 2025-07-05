package com.advisor.controller;

import com.advisor.dto.ChatDTO;
import com.advisor.dto.ChatRequest;
import com.advisor.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatController chatController;

    private ChatRequest chatRequest;

    @BeforeEach
    void setUp() {
        chatRequest = new ChatRequest();
        List<ChatDTO> messages = Arrays.asList(
                new ChatDTO("user", "Hello, how are you?")
        );
        chatRequest.setMessages(messages);
    }

    @Test
    void chat_shouldReturnFluxOfString() {
        Flux<String> mockResponseFlux = Flux.just("This", " is", " a", " test", " response.");
        when(chatService.getChatCompletion(chatRequest)).thenReturn(mockResponseFlux);

        Flux<String> resultFlux = chatController.chat(chatRequest);

        StepVerifier.create(resultFlux)
                .expectNext("This", " is", " a", " test", " response.")
                .verifyComplete();

        verify(chatService, times(1)).getChatCompletion(chatRequest);
        verifyNoMoreInteractions(chatService);
    }

    @Test
    void chat_shouldHandleEmptyResponse() {
        Flux<String> emptyResponseFlux = Flux.empty();
        when(chatService.getChatCompletion(chatRequest)).thenReturn(emptyResponseFlux);

        Flux<String> resultFlux = chatController.chat(chatRequest);

        StepVerifier.create(resultFlux)
                .expectComplete()
                .verify();

        verify(chatService, times(1)).getChatCompletion(chatRequest);
        verifyNoMoreInteractions(chatService);
    }

    @Test
    void chat_shouldHandleErrorResponse() {
        Flux<String> errorResponseFlux = Flux.error(new RuntimeException("Service unavailable"));
        when(chatService.getChatCompletion(chatRequest)).thenReturn(errorResponseFlux);

        Flux<String> resultFlux = chatController.chat(chatRequest);

        StepVerifier.create(resultFlux)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Service unavailable"))
                .verify();

        verify(chatService, times(1)).getChatCompletion(chatRequest);
        verifyNoMoreInteractions(chatService);
    }
}