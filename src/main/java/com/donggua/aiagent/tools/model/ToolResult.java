package com.donggua.aiagent.tools.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 结构化工具执行结果
 * 对应 OpenManus 的 ToolResult 模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolResult {

    /** 正常输出内容 */
    private String output;

    /** 错误信息（如果执行出错） */
    private String error;

    /** 系统级信息（如状态变更提示） */
    private String system;

    /** 是否执行成功 */
    public boolean isSuccess() {
        return error == null || error.isBlank();
    }

    /** 是否包含有效内容 */
    public boolean hasContent() {
        return (output != null && !output.isBlank())
                || (system != null && !system.isBlank())
                || (error != null && !error.isBlank());
    }

    public static ToolResult success(String output) {
        return ToolResult.builder().output(output).build();
    }

    public static ToolResult error(String error) {
        return ToolResult.builder().error(error).build();
    }

    public static ToolResult system(String system) {
        return ToolResult.builder().system(system).build();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (output != null) sb.append("输出：").append(output);
        if (error != null) sb.append(" 错误：").append(error);
        if (system != null) sb.append(" 系统：").append(system);
        return sb.toString();
    }
}
