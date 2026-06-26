package com.donggua.aiagent.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 人类交互工具
 * 对应 OpenManus AskHuman，当智能体需要人类输入时使用
 */
@Slf4j
public class AskHumanTool {

    @Tool(description = """
            Ask the human user for input, clarification, or additional information.
            Use this when you need more context, when a task requires human judgment,
            or when you encounter a situation you cannot handle automatically.
            """)
    public String askHuman(
            @ToolParam(description = "Question or request for the human user") String question,
            @ToolParam(description = "List of options/choices for the human to pick from (optional)") String[] options) {

        StringBuilder sb = new StringBuilder();
        sb.append("🤷 需要人类协助\n");
        sb.append("问题: ").append(question).append("\n");

        if (options != null && options.length > 0) {
            sb.append("可选方案:\n");
            for (int i = 0; i < options.length; i++) {
                sb.append("  ").append(i + 1).append(". ").append(options[i]).append("\n");
            }
        }

        sb.append("\n请人类用户提供信息/选择后继续执行。");

        log.info("AskHuman: {}", question);
        return sb.toString();
    }
}
