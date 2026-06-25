package com.donggua.aiagent.agent;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.donggua.aiagent.agent.model.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Author: ajie
 * Date: 2026-06-24 16:22
 * Description: <描述>
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent {

    // 可用工具
    private ToolCallback[] availableTools;

    // 保存工具调用的信息响应
    private ChatResponse toolCallChatResponse;

    // 工具调用管理者
    private final ToolCallingManager toolCallingManager;

    // 禁止内置的工具调用机制，自己维护上下文
    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] availableTools) {
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        this.chatOptions = DashScopeChatOptions.builder()
                // Sping Ai 禁止工具自动调用
                .withInternalToolExecutionEnabled(false)
                .build();
    }

    /**
     * 思考并判断是否有下一步操作
     *
     * @return
     */
    @Override
    public boolean think() {
        // 判断下一步提示词是否为空
        if (getNextStepPrompt() != null && !getNextStepPrompt().isEmpty()) {
            // 不为空 将下一步操作提示词放到消息列表中
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getMessagesList().add(userMessage);
        }
        // 将消息列表取出
        List<Message> messageList = getMessagesList();
        // 将options添加到消息列表中
        Prompt prompt = new Prompt(messageList, this.chatOptions);

        try {
            // 将消息列表、系统提示词、可用工具传给AI获取到AI的响应
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .toolCallbacks(availableTools)
                    .call()
                    .chatResponse();
            // 将响应赋值给toolCallChatResponse
            this.toolCallChatResponse = chatResponse;
            // 获取助手消息
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            // 获取工具列表
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
            String result = assistantMessage.getText();
            log.info(getName() + "的思考：" + result);
            log.info(getName() + "选择了" + toolCallList.size() + "个工具准备调用");
            String toolCallInfo = toolCallList.stream()
                    .map(toolCall -> String.format("工具名称：%s，参数：%s", toolCall.name(), toolCall.arguments()))
                    .collect(Collectors.joining("/n"));
            log.info(toolCallInfo);

            // 判断是否有需要调用的工具
            if (toolCallList.isEmpty()) {
                // 如果为空 才需要将助手提示词添加到上下文中
                getMessagesList().add(assistantMessage);
                return false;
            } else {
                // 返回true不需要记录 后续会自自动记录
                return true;
            }
        } catch (Exception e) {
            log.error(getName() + "的思考过程遇到了一些问题：" + e.getMessage());
            getMessagesList().add(new AssistantMessage("处理时遇到了错误：" + e.getMessage()));
            return false;
        }

    }

    /**
     * 执行工具调用 并记录结果
     *
     * @return
     */
    @Override
    public String act() {
        // 判断AI返回的响应中是否有工具需要调用
        if (!toolCallChatResponse.hasToolCalls()) {
            return "没有需要调用的工具";
        }
        // 调用工具
        Prompt prompt = new Prompt(getMessagesList(), this.chatOptions);
        // 调用工具获取到工具调用的返回值
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
        // 将工具调用的上下文写入到消息列表中
        setMessagesList(toolExecutionResult.conversationHistory());
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());

        // 判断是否调用了终止工具
        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                .anyMatch(toolResponse -> toolResponse.name().matches("doTerminate"));
        if (terminateToolCalled) {
            // 终止任务，修改状态
            setState(AgentState.FINISH);
        }

        String result = toolResponseMessage.getResponses().stream()
                .map(toolResponse -> "工具：" + toolResponse.name() + "返回的结果：" + toolResponse.responseData())
                .collect(Collectors.joining("/n"));
        log.info(result);
        return result;
    }
}
