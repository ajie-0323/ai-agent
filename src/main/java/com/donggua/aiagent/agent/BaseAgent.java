package com.donggua.aiagent.agent;

import com.donggua.aiagent.agent.model.AgentState;
import com.esotericsoftware.minlog.Log;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.util.StringUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Author: ajie
 * Date: 2026-06-24 16:21
 * 抽象代理类 用于管理代理状态和执行流程
 * <p>
 * 提供状态转换，内存管理和基于步骤的执行循环的基础功能
 * 子了必须实现step方法
 */
@Data
@Slf4j
public abstract class BaseAgent {

    // 核心属性
    private String name;

    // 提示词
    private String systemPrompt;
    private String nextStepPrompt;

    // 执行状态
    private AgentState state = AgentState.IDLE;

    // 执行控制
    private int currentStep = 0;
    private int maxSteps = 10;

    // LLM
    private ChatClient chatClient;

    // Memory （手动管理上下文列表）
    private List<Message> messagesList = new ArrayList<>();

    /**
     * 运行代理
     *
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public String run(String userPrompt) {
        // 基础校验
        if (this.state != AgentState.IDLE) {
            throw new RuntimeException("Cannot run agent from state:" + this.state);
        }
        if (StringUtil.isEmpty(userPrompt)) {
            throw new RuntimeException("Cannot run agent with user pompt");
        }

        // 修改状态
        this.state = AgentState.RUNNING;

        // 返回结果
        List<String> results = new ArrayList<>();

        // 将用户的提示词存到上下文中
        messagesList.add(new UserMessage(userPrompt));

        try {
            // 执行
            for (int i = 0; i < maxSteps && this.state != AgentState.FINISH; i++) {
                // 基数
                int currentNum = i + 1;
                currentStep = currentNum;

                log.info("Executing step {}/{}", currentNum, maxSteps);

                // 单步操作执行
                String stepResult = step();
                String result = "Step " + currentNum + "：" + stepResult;
                results.add(result);
            }

            // 判断是否超出限制
            if (this.currentStep >= maxSteps) {
                state = AgentState.FINISH;
                results.add("Terminated: Reached max steps (" + maxSteps + ")");
            }

            return String.join("\n", results);
        } catch (Exception e) {
            state = AgentState.ERROR;
            log.error("Error executing agent:", e);
            return "执行错误：" + e.getMessage();
        } finally {
            // 清理资源
            this.cleanup();
        }
    }

    /**
     * 运行代理 (流式输出)
     *
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public SseEmitter runStream(String userPrompt) {
        // 创建一个超长时间等待的SseEmitter
        SseEmitter sseEmitter = new SseEmitter(300000L);

        // 使用异步等待获取每一次调用的信息
        CompletableFuture.runAsync(() -> {
            // 基础校验
            try {
                if (this.state != AgentState.IDLE) {
                    sseEmitter.send("错误：无法从状态运行代理" + this.state);
                    sseEmitter.complete();
                }
                if (StringUtil.isEmpty(userPrompt)) {
                    sseEmitter.send("错误：不能使用空提示词代理");
                    sseEmitter.complete();
                }
            } catch (Exception e) {
                sseEmitter.completeWithError(e);
            }

            // 修改状态
            this.state = AgentState.RUNNING;

            // 返回结果
            List<String> results = new ArrayList<>();

            // 将用户的提示词存到上下文中
            messagesList.add(new UserMessage(userPrompt));

            try {
                // 执行
                for (int i = 0; i < maxSteps && this.state != AgentState.FINISH; i++) {
                    // 基数
                    int currentNum = i + 1;
                    currentStep = currentNum;

                    log.info("Executing step {}/{}", currentNum, maxSteps);

                    // 单步操作执行
                    String stepResult = step();
                    String result = "Step " + currentNum + "：" + stepResult;
                    results.add(result);
                    sseEmitter.send(result);
                }

                // 判断是否超出限制
                if (this.currentStep >= maxSteps) {
                    state = AgentState.FINISH;
                    results.add("Terminated: Reached max steps (" + maxSteps + ")");
                    sseEmitter.send("执行结束：达到最大步骤（" + maxSteps + "）");
                }

            } catch (Exception e) {
                state = AgentState.ERROR;
                log.error("Error executing agent:", e);
                try {
                    sseEmitter.send("执行错误：" + e.getMessage());
                    sseEmitter.complete();
                } catch (IOException ex) {
                    sseEmitter.completeWithError(ex);
                }
            } finally {
                // 清理资源
                this.cleanup();
            }

        });

        // 设置超时回调
        sseEmitter.onTimeout(() -> {
            // 修改运行状态
            this.state = AgentState.ERROR;
            this.cleanup();
            log.warn("SSE connection timeout");
        });

        // 设置完成回调
        sseEmitter.onCompletion(() -> {
            // 如果是运行中状态 修改成完成状态
            if (this.state == AgentState.RUNNING) {
                this.state = AgentState.FINISH;
            }
            this.cleanup();
            log.info("SSE connection finish");
        });

        return sseEmitter;
    }

    /**
     * 执行单个步骤
     *
     * @return 步骤执行结果
     */
    public abstract String step();

    /**
     * 清除资源
     */
    protected void cleanup() {
        // 手动清理资源
    }

}
