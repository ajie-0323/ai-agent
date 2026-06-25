package com.donggua.aiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.*;

/**
 * 违禁词检查 Advisor
 * 1. 检查用户输入是否包含违禁词
 * 2. 检查AI返回内容是否包含违禁词
 * 3. 包含违禁词直接抛出异常，阻止请求/响应
 *
 * @author donggua
 */
@Slf4j
@Component
public class ForbiddenWordAdvisor implements CallAdvisor, StreamAdvisor {

    /**
     * 违禁词列表（可配置化、从数据库/配置中心读取）
     */
    private static final Set<String> FORBIDDEN_WORDS = new HashSet<>(Arrays.asList(
            "赌博", "毒品", "暴力", "色情", "诈骗", "恐怖", "自杀", "武器", "暗杀", "违法"
    ));

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 执行顺序：比日志Advisor更早执行（优先拦截违禁词）
     */
    @Override
    public int getOrder() {
        return -1;
    }

    // ==================== 同步调用拦截 ====================
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        // 1. 检查用户输入违禁词
        checkUserInputForbiddenWords(request);

        // 2. 正常调用AI
        ChatClientResponse response = chain.nextCall(request);

        // 3. 检查AI返回违禁词
        checkAiResponseForbiddenWords(response);

        return response;
    }

    // ==================== 流式调用拦截 ====================
    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain chain) {
        // 1. 检查用户输入违禁词
        checkUserInputForbiddenWords(request);

        // 2. 流式调用AI
        return chain.nextStream(request)
                .doOnNext(this::checkAiResponseForbiddenWords);
    }

    // ==================== 核心检查逻辑 ====================

    /**
     * 检查用户输入消息
     */
    private void checkUserInputForbiddenWords(ChatClientRequest request) {
        List<Message> messages = request.prompt().getInstructions();
        for (Message message : messages) {
            if (message instanceof UserMessage userMessage) {
                String content = userMessage.getText();
                checkForbidden(content, "用户输入包含违禁词，请求已拦截");
            }
        }
    }

    /**
     * 检查AI返回消息
     */
    private void checkAiResponseForbiddenWords(ChatClientResponse response) {
        ChatResponse chatResponse = response.chatResponse();
        if (chatResponse == null || chatResponse.getResults() == null) {
            return;
        }

        for (Generation generation : chatResponse.getResults()) {
            AssistantMessage assistantMessage = generation.getOutput();
            if (assistantMessage != null) {
                String content = assistantMessage.getText();
                checkForbidden(content, "AI返回内容包含违禁词，响应已拦截");
            }
        }
    }

    /**
     * 违禁词匹配逻辑（包含即命中）
     */
    private void checkForbidden(String content, String errorMsg) {
        if (content == null || content.isBlank()) {
            return;
        }

        // 不区分大小写检查
        String lowerContent = content.toLowerCase();
        for (String word : FORBIDDEN_WORDS) {
            if (lowerContent.contains(word.toLowerCase())) {
                log.error("{} | 违禁词：{} | 内容：{}", errorMsg, word, content);
                throw new IllegalArgumentException(errorMsg + "：" + word);
            }
        }
    }
}