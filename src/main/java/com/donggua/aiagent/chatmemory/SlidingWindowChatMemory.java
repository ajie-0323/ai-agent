package com.donggua.aiagent.chatmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 滑动窗口记忆
 * 只保留最近 N 条消息，超出时自动丢弃最早的消息
 * 对应 OpenManus 的 Memory 管理（滑动窗口策略）
 */
@Slf4j
public class SlidingWindowChatMemory implements ChatMemory {

    private final Map<String, List<Message>> conversations = new ConcurrentHashMap<>();
    private final int maxMessages;

    public SlidingWindowChatMemory(int maxMessages) {
        this.maxMessages = maxMessages;
        log.info("初始化滑动窗口记忆，最大消息数: {}", maxMessages);
    }

    @Override
    public void add(String conversationId, Message message) {
        List<Message> messages = conversations.computeIfAbsent(conversationId, k -> new ArrayList<>());
        messages.add(message);
        trimConversation(messages);
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        List<Message> existing = conversations.computeIfAbsent(conversationId, k -> new ArrayList<>());
        existing.addAll(messages);
        trimConversation(existing);
    }

    @Override
    public List<Message> get(String conversationId) {
        return conversations.getOrDefault(conversationId, new ArrayList<>());
    }

    @Override
    public void clear(String conversationId) {
        conversations.remove(conversationId);
        log.info("清除会话记忆: {}", conversationId);
    }

    /**
     * 修剪消息列表，保留最近的 maxMessages 条
     */
    private void trimConversation(List<Message> messages) {
        if (messages.size() > maxMessages) {
            int removeCount = messages.size() - maxMessages;
            List<Message> removed = new ArrayList<>(messages.subList(0, removeCount));
            messages.subList(0, removeCount).clear();
            log.debug("滑动窗口修剪: 移除了 {} 条旧消息", removeCount);
        }
    }

    /**
     * 获取当前会话数
     */
    public int getConversationCount() {
        return conversations.size();
    }
}
