package com.donggua.aiagent.controller;

import com.donggua.aiagent.agent.Manus;
import com.donggua.aiagent.app.LoveApp;
import com.donggua.aiagent.common.ResponseResult;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * Author: ajie
 * Date: 2026-06-25 14:34
 * Description: AI 交互接口
 */
@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private LoveApp loveApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

    /**
     * 同步调用 AI 恋爱大师
     *
     * @param message 用户消息
     * @param chatId  会话 ID
     * @return 统一响应结果
     */
    @GetMapping("/love_app/chat/sync")
    public ResponseResult<String> doChatWithLoveAppSync(String message, String chatId) {
        String result = loveApp.doChat(message, chatId);
        return ResponseResult.success(result);
    }

    /**
     * SSE 调用 AI 恋爱大师（流式输出）
     *
     * @param message 用户消息
     * @param chatId  会话 ID
     * @return SSE 事件流
     */
    @GetMapping(value = "/love_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSSE(String message, String chatId) {
        return loveApp.doChatByStream(message, chatId);
    }

    /**
     * ServerSentEvent 格式调用 AI 恋爱大师（流式输出）
     *
     * @param message 用户消息
     * @param chatId  会话 ID
     * @return ServerSentEvent 流
     */
    @GetMapping(value = "/love_app/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> doChatWithLoveAppServerSentEvent(String message, String chatId) {
        return loveApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build()
                );
    }

    /**
     * SseEmitter 格式调用 AI 恋爱大师（流式输出）
     *
     * @param message 用户消息
     * @param chatId  会话 ID
     * @return SseEmitter
     */
    @GetMapping(value = "/love_app/chat/sse_emitter")
    public SseEmitter doChatWithLoveAppSseEmitter(String message, String chatId) {
        SseEmitter sseEmitter = new SseEmitter(180000L);

        loveApp.doChatByStream(message, chatId)
                .subscribe(chunk -> {
                            try {
                                sseEmitter.send(chunk);
                            } catch (IOException e) {
                                sseEmitter.completeWithError(e);
                            }
                        },
                        sseEmitter::completeWithError,
                        sseEmitter::complete);
        return sseEmitter;
    }

    /**
     * 流式调用 Manus 超级智能体
     *
     * @param message 用户消息
     * @return SseEmitter
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithLoveAppManus(String message) {
        Manus manus = new Manus(allTools, dashscopeChatModel);
        return manus.runStream(message);
    }
}
