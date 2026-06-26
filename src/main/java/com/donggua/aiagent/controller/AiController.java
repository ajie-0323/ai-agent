package com.donggua.aiagent.controller;

import com.donggua.aiagent.agent.Manus;
import com.donggua.aiagent.app.LoveApp;
import com.donggua.aiagent.chatmemory.FileBasesChatMemory;
import com.donggua.aiagent.common.ResponseResult;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private LoveApp loveApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

    private static final String MEMORY_DIR = System.getProperty("user.dir") + "/tmp/chat-memory";

    // ==================== Love Master ====================

    @GetMapping("/love_app/chat/sync")
    public ResponseResult<String> doChatWithLoveAppSync(String message, String chatId) {
        String result = loveApp.doChat(message, chatId);
        return ResponseResult.success(result);
    }

    @GetMapping(value = "/love_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSSE(String message, String chatId) {
        return loveApp.doChatByStream(message, chatId);
    }

    @GetMapping(value = "/love_app/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> doChatWithLoveAppServerSentEvent(String message, String chatId) {
        return loveApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder().data(chunk).build());
    }

    @GetMapping(value = "/love_app/chat/sse_emitter")
    public SseEmitter doChatWithLoveAppSseEmitter(String message, String chatId) {
        SseEmitter sseEmitter = new SseEmitter(180000L);
        loveApp.doChatByStream(message, chatId)
                .subscribe(chunk -> {
                            try { sseEmitter.send(chunk); }
                            catch (IOException e) { sseEmitter.completeWithError(e); }
                        },
                        sseEmitter::completeWithError,
                        sseEmitter::complete);
        return sseEmitter;
    }

    // ==================== Manus ====================

    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(@RequestParam String message,
                                      @RequestParam(required = false) String chatId) {
        if (chatId == null || chatId.isBlank()) {
            chatId = "manus_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        }
        ChatMemory chatMemory = new FileBasesChatMemory(MEMORY_DIR);
        Manus manus = new Manus(allTools, dashscopeChatModel);
        manus.setChatMemory(chatMemory);
        manus.setConversationId(chatId);
        return manus.runStream(message);
    }

    @GetMapping("/manus/chat/sync")
    public ResponseResult<String> doChatWithManusSync(@RequestParam String message,
                                                       @RequestParam(required = false) String chatId) {
        if (chatId == null || chatId.isBlank()) {
            chatId = "manus_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        }
        ChatMemory chatMemory = new FileBasesChatMemory(MEMORY_DIR);
        Manus manus = new Manus(allTools, dashscopeChatModel);
        manus.setChatMemory(chatMemory);
        manus.setConversationId(chatId);
        String result = manus.run(message);
        return ResponseResult.success(result);
    }

    @GetMapping("/manus/history")
    public ResponseResult<List<Map<String, Object>>> getManusHistory(@RequestParam String chatId) {
        ChatMemory chatMemory = new FileBasesChatMemory(MEMORY_DIR);
        List<Message> messages = chatMemory.get(chatId);
        List<Map<String, Object>> result = messages.stream()
                .map(msg -> {
                    Map<String, Object> item = new HashMap<>();
                    if (msg instanceof UserMessage) {
                        item.put("role", "user");
                        item.put("content", ((UserMessage) msg).getText());
                    } else if (msg instanceof AssistantMessage) {
                        item.put("role", "assistant");
                        String text = ((AssistantMessage) msg).getText();
                        item.put("content", text != null ? text : "");
                    } else {
                        item.put("role", "system");
                        item.put("content", msg.getText());
                    }
                    return item;
                })
                .collect(Collectors.toList());
        return ResponseResult.success(result);
    }

    @GetMapping("/love_app/history")
    public ResponseResult<List<Map<String, Object>>> getLoveHistory(@RequestParam String chatId) {
        ChatMemory chatMemory = new FileBasesChatMemory(MEMORY_DIR);
        List<Message> messages = chatMemory.get(chatId);
        List<Map<String, Object>> result = messages.stream()
                .map(msg -> {
                    Map<String, Object> item = new HashMap<>();
                    if (msg instanceof UserMessage) {
                        item.put("role", "user");
                        item.put("content", ((UserMessage) msg).getText());
                    } else if (msg instanceof AssistantMessage) {
                        item.put("role", "assistant");
                        String text = ((AssistantMessage) msg).getText();
                        item.put("content", text != null ? text : "");
                    } else {
                        item.put("role", "system");
                        item.put("content", msg.getText());
                    }
                    return item;
                })
                .collect(Collectors.toList());
        return ResponseResult.success(result);
    }
}
