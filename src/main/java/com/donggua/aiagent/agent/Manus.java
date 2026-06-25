package com.donggua.aiagent.agent;

import com.donggua.aiagent.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

/**
 * Author: ajie
 * Date: 2026-06-24 16:23
 * Description: AI超级智能体 （拥有自主规划能力）
 */
@Component
public class Manus extends ToolCallAgent {

    public Manus(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
        // 将工具传递到父类
        super(allTools);

        // 设置名称
        this.setName("Manus");

        //初始化提示词
        String SYSTEM_PROMPT = """
                You are Manus, an all-capable AI assistant, aimed at solving any task presented by the user.
                You have various tools at your disposal that you can call upon to efficiently complete complex requests. 
                Whether it's programming, information retrieval, file processing, web browsing, 
                or human interaction (only for extreme cases), you can handle it all.
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);

        String NEXT_STEP_PROMPT = """
                Based on user needs, proactively select the most appropriate tool or combination of tools. 
                For complex tasks, you can break down the problem and use different tools step by step to solve it. 
                After using each tool, clearly explain the execution results and suggest the next steps.
                If you want to stop the interaction at any point, use the `terminate` tool/function call.
                """;
        this.setNextStepPrompt(NEXT_STEP_PROMPT);

        // 初始化ChatClient
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();

        this.setChatClient(chatClient);

        // 设置最大循环次数
        this.setMaxSteps(20);

    }
}
