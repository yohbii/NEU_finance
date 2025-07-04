package com.advisor.service;

import com.advisor.dto.ChatRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

import com.cjcrafter.openai.OpenAI;
import com.cjcrafter.openai.chat.*;
import com.cjcrafter.openai.chat.tool.Function;
import com.cjcrafter.openai.chat.tool.FunctionParameters;
import com.cjcrafter.openai.chat.tool.Tool;
import com.cjcrafter.openai.chat.tool.ToolCall;

@Service
@Slf4j
public class ChatService {

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url}")
    private String baseUrl;

    private OpenAI openai;

    @Autowired
    private FunctionExecutorService functionExecutorService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        this.openai = OpenAI.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .build();
    }

    // 更智能的 system prompt，鼓励模型优先调用工具函数：
    private static final String SYSTEM_PROMPT = "你是一个智能投顾助手，具备丰富的金融知识和工具调用能力。遇到涉及基金、策略、因子等具体信息查询、分析、推荐等问题时，请优先调用相关工具函数获取最准确的数据和答案，并将工具返回结果用于回复用户。仅在工具无法满足需求或问题与工具无关时，才直接用自然语言回复。";

    public Flux<String> getChatCompletion(ChatRequest request) {

        /* ---------- 1. 组装对话消息 ---------- */
        List<Object> messages = request.getMessages().stream()
                .map(msg -> {
                    // 组装 message 为 Map 结构
                    return java.util.Map.of(
                        "role", msg.getRole(),
                        "content", msg.getContent()
                    );
                })
                .collect(Collectors.toList());
        // 添加 system prompt
        messages.add(0, java.util.Map.of(
            "role", "system",
            "content", SYSTEM_PROMPT
        ));

        /* ---------- 2. 组装工具（手动构造 JSON） ---------- */
        List<Object> tools = functionExecutorService.getFunctions().stream()
                .map(chatFunction -> {
                    // chatFunction.getParameters() 必须为 Map
                    Object paramsObj = chatFunction.getParameters();
                    java.util.Map<String, Object> parameters;
                    if (paramsObj instanceof java.util.Map) {
                        parameters = (java.util.Map<String, Object>) paramsObj;
                    } else {
                        // 转换为 Map
                        parameters = objectMapper.convertValue(paramsObj, java.util.Map.class);
                    }
                    java.util.Map<String, Object> function = new java.util.HashMap<>();
                    function.put("name", chatFunction.getName());
                    function.put("description", chatFunction.getDescription());
                    function.put("parameters", parameters);
                    java.util.Map<String, Object> tool = new java.util.HashMap<>();
                    tool.put("type", "function");
                    tool.put("function", function);
                    return tool;
                })
                .collect(Collectors.toList());

        /* ---------- 3. 手动构造请求体并 POST ---------- */
        java.util.Map<String, Object> requestBody = new java.util.HashMap<>();
        requestBody.put("model", "deepseek-chat");
        requestBody.put("messages", messages);
        requestBody.put("tools", tools);

        // debug: 打印实际请求 JSON
        try {
            String debugJson = objectMapper.writeValueAsString(requestBody);
            log.error("DEBUG: 请求JSON: " + debugJson);
        } catch (Exception e) {
            log.error("DEBUG: 序列化请求JSON失败", e);
        }

        return Flux.create(sink -> {
            try {
                // 用 WebClient 直接 POST
                org.springframework.web.reactive.function.client.WebClient client = org.springframework.web.reactive.function.client.WebClient.builder()
                        .baseUrl(baseUrl + "/v1/chat/completions")
                        .defaultHeader("Authorization", "Bearer " + apiKey)
                        .build();
                client.post()
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(String.class)
                        .subscribe(
                            resp -> {
                                try {
                                    // 只提取 content 字段
                                    com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(resp);
                                    String content = root.path("choices").get(0).path("message").path("content").asText("");
                                    // 只返回纯文本，不加 data: 前缀，由 Controller 层或框架自动处理 SSE 包装
                                    sink.next(objectMapper.writeValueAsString(java.util.Map.of("content", content)) + "\n\n");
                                } catch (Exception e) {
                                    log.error("Error parsing deepseek response", e);
                                    sink.error(e);
                                }
                            },
                            err -> {
                                log.error("Error during chat completion", err);
                                sink.error(err);
                            },
                            sink::complete
                        );
            } catch (Exception e) {
                log.error("Error during chat completion", e);
                sink.error(e);
            }
        });
    }

    /* ——— 工具调用后的二次请求 & SSE 输出 ——— */
    private void handleResponse(ChatResponse response,
                                List<ChatMessage> messages,
                                reactor.core.publisher.FluxSink<String> sink) throws Exception {

        ChatChoice choice = response.get(0);
        ChatMessage responseMessage = choice.getMessage();

        // 3.1 若模型要求调用工具
        if (responseMessage.hasToolCalls()) {
            for (ToolCall toolCall : responseMessage.getToolCalls()) {
                if (toolCall instanceof com.cjcrafter.openai.chat.tool.FunctionToolCall fcall) {

                    /* --- 调用本地函数 --- */
                    Object result = functionExecutorService.execute(
                            fcall.getFunction().getName(),
                            fcall.getFunction().getArguments()
                    );

                    /* --- 将结果序列化为 JSON 作为 tool 消息 --- */
                    String resultJson;
                    try {
                        resultJson = objectMapper.writeValueAsString(result);
                    } catch (JsonProcessingException e) {
                        log.error("Error serializing function result to JSON", e);
                        resultJson = "{\"error\":\"Error processing function result\"}";
                    }

                    /* --- 增量对话历史 --- */
                    messages.add(responseMessage);                             // assistant (tool call)
                    messages.add(ChatMessage.toToolMessage(resultJson,         // tool
                            toolCall.getId()));

                    /* --- 二次请求，让模型读取工具返回 --- */
                    ChatResponse finalResponse = openai.createChatCompletion(
                            com.cjcrafter.openai.chat.ChatRequest.builder()
                                    .model("deepseek-chat")
                                    .messages(messages)
                                    .build()
                    );

                    /* --- SSE 输出 --- */
                    sink.next("data: "
                            + finalResponse.get(0).getMessage().getContent()
                            + "\n\n");
                }
            }
        }
        // 3.2 普通回答
        else if (responseMessage.getContent() != null) {
            sink.next("data: " + responseMessage.getContent() + "\n\n");
        }
        sink.complete();
    }
}
