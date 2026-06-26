package com.donggua.aiagent.tools;

import com.donggua.aiagent.tools.model.ToolResult;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 工具注册中心
 * 注册所有智能体可用的工具
 */
@Configuration
public class ToolRegistration {

    @Value("${search-api.api-key}")
    private String API_KEY;

    @Bean
    public ToolCallback[] allTools() {
        return ToolCallbacks.from(
                // ===== 原有工具 =====
                new FileOperationTool(),
                new WebSearchTool(API_KEY),
                new WebScrapingTool(),
                new ResourceDownloadTool(),
                new TerminalOperationTool(),
                new PDFGenerationTool(),
                new TerminateTool(),

                // ===== 新增工具（对应 OpenManus 能力） =====
                new PlanningTool(),
                new PythonExecuteTool(),
                new AskHumanTool(),
                new StrReplaceEditorTool()
        );
    }
}
