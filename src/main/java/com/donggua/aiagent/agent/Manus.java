package com.donggua.aiagent.agent;

import com.donggua.aiagent.advisor.MyLoggerAdvisor;
import com.donggua.aiagent.flow.PlanningFlow;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

/**
 * Author: ajie
 * Date: 2026-06-24 16:23
 * Description: AI 超级智能体（拥有自主规划能力）
 */
@Component
public class Manus extends ToolCallAgent {

    private PlanningFlow planningFlow;

    public Manus(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
        super(allTools);
        this.setName("Manus");

        // 提示词：要求 markdown 格式输出代码/文件
        String SYSTEM_PROMPT = """
                You are Manus, an all-capable AI assistant, aimed at solving any task presented by the user.
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.
                Whether it's programming, information retrieval, file processing, web browsing, data analysis,
                or human interaction (only for extreme cases), you can handle it all.

                IMPORTANT: When showing code or file content, ALWAYS use markdown code blocks:
                - Use ```language for code blocks with proper language name
                - Use ``` for plain text file content
                - Use `inline code` for short code references
                - When you create a file, clearly tell the user the file name and path
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);

        String NEXT_STEP_PROMPT = """
                Based on user needs, proactively select the most appropriate tool or combination of tools.
                For complex tasks, you can break down the problem and use different tools step by step to solve it.
                After using each tool, clearly explain the execution results and suggest the next steps.

                Available tools include:
                - File operations (read/write files)
                - Web search and scraping
                - Resource download
                - Terminal command execution
                - PDF generation
                - **Task planning** (use planning tool to create and track plans for complex tasks)
                - **Python code execution** (execute Python code for data analysis and scripting)
                - **Smart file editing** (view, create, edit files with precision)
                - **Ask human** (ask for human input when needed)

                For complex multi-step tasks, first use the planning tool to create a plan,
                then execute each step according to the plan.

                IMPORTANT: When presenting code or file content:
                - Wrap code blocks with ```language (e.g. ```python, ```java, ```html)
                - When you create/save a file, tell the user the exact file NAME and that it can be downloaded
                - After using a tool, summarize what was accomplished

                If you want to stop the interaction at any point, use the `terminate` tool/function call.
                """;
        this.setNextStepPrompt(NEXT_STEP_PROMPT);

        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();
        this.setChatClient(chatClient);
        this.setMaxSteps(20);
    }

    public String executeWithPlanning(String userPrompt) {
        this.planningFlow = new PlanningFlow(this);
        return planningFlow.execute(userPrompt);
    }

    public PlanningFlow getPlanningFlow() {
        return planningFlow;
    }
}
