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
        List<Object> messages = request.getMessages().stream()
                .map(msg -> java.util.Map.of(
                        "role", msg.getRole(),
                        "content", msg.getContent()
                ))
                .collect(Collectors.toList());
        messages.add(0, java.util.Map.of(
                "role", "system",
                "content", SYSTEM_PROMPT
        ));
        List<Object> tools = functionExecutorService.getFunctions().stream()
                .map(chatFunction -> {
                    Object paramsObj = chatFunction.getParameters();
                    java.util.Map<String, Object> parameters;
                    if (paramsObj instanceof java.util.Map) {
                        parameters = (java.util.Map<String, Object>) paramsObj;
                    } else {
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

        return Flux.create(sink -> {
            processFunctionCalling(messages, tools, sink);
        });
    }

    // 参考 deepseek 官方 function calling 流程递归处理
    private void processFunctionCalling(List<Object> messages, List<Object> tools, reactor.core.publisher.FluxSink<String> sink) {
        java.util.Map<String, Object> requestBody = new java.util.HashMap<>();
        requestBody.put("model", "deepseek-chat");
        requestBody.put("messages", messages);
        requestBody.put("tools", tools);
        org.springframework.web.reactive.function.client.WebClient client = org.springframework.web.reactive.function.client.WebClient.builder()
                .baseUrl(baseUrl + "/chat/completions")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
        client.post()
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(resp -> {
                    try {
                        log.info("[DeepseekRawResp] {}", resp);
                        com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(resp);
                        com.fasterxml.jackson.databind.JsonNode choice = root.path("choices").get(0);
                        com.fasterxml.jackson.databind.JsonNode message = choice.path("message");
                        // 检查是否有 tool_calls
                        com.fasterxml.jackson.databind.JsonNode toolCalls = message.path("tool_calls");
                        if (toolCalls.isArray() && toolCalls.size() > 0) {
                            // 日志：记录每次 function call 的详细信息
                            log.info("[FunctionCall] tool_calls detected, count: {}", toolCalls.size());
                            for (com.fasterxml.jackson.databind.JsonNode toolCall : toolCalls) {
                                String functionName = toolCall.path("function").path("name").asText("");
                                String arguments = toolCall.path("function").path("arguments").asText("");
                                log.info("[FunctionCall] 调用本地函数: {}，参数: {}", functionName, arguments);
                                Object result = functionExecutorService.execute(functionName, arguments);
                                log.info("[FunctionCall] 函数 {} 返回: {}", functionName, result);
                                String resultJson = objectMapper.writeValueAsString(result);
                                // 追加 assistant (tool call) 消息
                                java.util.Map<String, Object> assistantMsg = new java.util.HashMap<>();
                                assistantMsg.put("role", "assistant");
                                assistantMsg.put("content", null);
                                assistantMsg.put("tool_calls", java.util.List.of(toolCall));
                                messages.add(assistantMsg);
                                // 追加 tool 消消息
                                java.util.Map<String, Object> toolMsg = new java.util.HashMap<>();
                                toolMsg.put("role", "tool");
                                toolMsg.put("content", resultJson);
                                toolMsg.put("tool_call_id", toolCall.path("id").asText(""));
                                messages.add(toolMsg);
                            }
                            // 递归再次请求
                            processFunctionCalling(messages, tools, sink);
                            return;
                        }
                        // 只有没有 tool_calls 时才输出到前端
                        String content = message.path("content").asText("");
                        String json = objectMapper.writeValueAsString(java.util.Map.of("content", content));
                        sink.next(json + "\n\n");
                        sink.complete();
                    } catch (Exception e) {
                        log.error("Error parsing deepseek response", e);
                        sink.error(e);
                    }
                }, err -> {
                    log.error("Error during chat completion", err);
                    sink.error(err);
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
