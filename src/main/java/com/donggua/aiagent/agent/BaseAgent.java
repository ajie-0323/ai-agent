package com.donggua.aiagent.agent;

import com.donggua.aiagent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.util.StringUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
@Slf4j
public abstract class BaseAgent {

    private String name;
    private String systemPrompt;
    private String nextStepPrompt;
    private AgentState state = AgentState.IDLE;
    private int currentStep = 0;
    private int maxSteps = 10;
    private ChatClient chatClient;
    private List<Message> messagesList = new ArrayList<>();

    private ChatMemory chatMemory;
    private String conversationId;

    public String run(String userPrompt) {
        if (this.state != AgentState.IDLE) {
            throw new RuntimeException("Cannot run agent from state:" + this.state);
        }
        if (StringUtil.isEmpty(userPrompt)) {
            throw new RuntimeException("Cannot run agent with empty prompt");
        }
        this.state = AgentState.RUNNING;
        List<String> results = new ArrayList<>();
        messagesList.add(new UserMessage(userPrompt));
        try {
            for (int i = 0; i < maxSteps && this.state != AgentState.FINISH; i++) {
                int currentNum = i + 1;
                currentStep = currentNum;
                log.info("Executing step {}/{}", currentNum, maxSteps);
                String stepResult = step();
                results.add("Step " + currentNum + ": " + stepResult);
            }
            if (this.currentStep >= maxSteps) {
                state = AgentState.FINISH;
                results.add("Terminated: Reached max steps (" + maxSteps + ")");
            }
            saveMemory();
            return String.join("\n", results);
        } catch (Exception e) {
            state = AgentState.ERROR;
            log.error("Error executing agent:", e);
            return "Error: " + e.getMessage();
        } finally {
            this.cleanup();
        }
    }

    public SseEmitter runStream(String userPrompt) {
        SseEmitter sseEmitter = new SseEmitter(300000L);
        CompletableFuture.runAsync(() -> {
            try {
                if (this.state != AgentState.IDLE) {
                    sseEmitter.send("Error: cannot run agent from state " + this.state);
                    sseEmitter.complete();
                    return;
                }
                if (StringUtil.isEmpty(userPrompt)) {
                    sseEmitter.send("Error: empty prompt");
                    sseEmitter.complete();
                    return;
                }
            } catch (Exception e) {
                sseEmitter.completeWithError(e);
                return;
            }
            this.state = AgentState.RUNNING;
            loadMemory();
            List<String> results = new ArrayList<>();
            messagesList.add(new UserMessage(userPrompt));
            try {
                for (int i = 0; i < maxSteps && this.state != AgentState.FINISH; i++) {
                    int currentNum = i + 1;
                    currentStep = currentNum;
                    log.info("Executing step {}/{}", currentNum, maxSteps);
                    String stepResult = step();
                    String result = "Step " + currentNum + ": " + stepResult;
                    results.add(result);
                    sseEmitter.send(result);
                }
                if (this.currentStep >= maxSteps) {
                    state = AgentState.FINISH;
                    results.add("Terminated: Reached max steps (" + maxSteps + ")");
                    sseEmitter.send("Reached max steps (" + maxSteps + ")");
                }
                saveMemory();
            } catch (Exception e) {
                state = AgentState.ERROR;
                log.error("Error executing agent:", e);
                try {
                    sseEmitter.send("Error: " + e.getMessage());
                } catch (IOException ex) {
                    sseEmitter.completeWithError(ex);
                }
            } finally {
                this.cleanup();
            }
            try {
                sseEmitter.complete();
            } catch (Exception ignored) {}
        });
        sseEmitter.onTimeout(() -> {
            this.state = AgentState.ERROR;
            this.cleanup();
            log.warn("SSE connection timeout");
        });
        sseEmitter.onCompletion(() -> {
            if (this.state == AgentState.RUNNING) {
                this.state = AgentState.FINISH;
            }
            this.cleanup();
            log.info("SSE connection finish");
        });
        return sseEmitter;
    }

    protected void loadMemory() {
        if (chatMemory != null && conversationId != null) {
            try {
                List<Message> history = chatMemory.get(conversationId);
                if (history != null && !history.isEmpty()) {
                    messagesList.addAll(history);
                    log.info("Loaded {} history messages (session: {})", history.size(), conversationId);
                }
            } catch (Exception e) {
                log.warn("Load history failed: {}", e.getMessage());
            }
        }
    }

    protected void saveMemory() {
        if (chatMemory != null && conversationId != null && !messagesList.isEmpty()) {
            try {
                chatMemory.clear(conversationId);
                for (Message msg : messagesList) {
                    chatMemory.add(conversationId, msg);
                }
                log.info("Saved {} messages to memory (session: {})", messagesList.size(), conversationId);
            } catch (Exception e) {
                log.warn("Save memory failed: {}", e.getMessage());
            }
        }
    }

    public abstract String step();

    protected void cleanup() {}
}
