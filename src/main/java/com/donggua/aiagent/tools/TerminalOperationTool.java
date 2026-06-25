package com.donggua.aiagent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 终端操作工具
 */
public class TerminalOperationTool {

    /**
     * 传入终端操作命令 执行并且返回
     *
     * @param command
     * @return
     */
    @Tool(description = "Execute a command in the terminal")
    public String executeTerminalCommand(@ToolParam(description = "Command to execute in the terminal") String command) {
        // 创建一个builder来存取信息
        StringBuilder output = new StringBuilder();
        try {
            // 通过命令获取进程
            Process process = Runtime.getRuntime().exec(new String[]{"cmd", "/c", command});
            // Process process = Runtime.getRuntime().exec(command);
            // 通过输入流获取到每一行流信息
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                // 逐行读取
                while ((line = reader.readLine()) != null) {
                    // 将每一行读取到输出对象中
                    output.append(line).append("\n");
                }
            }
            // 线程等待直到完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                output.append("Command execution failed with exit code: ").append(exitCode);
            }
        } catch (IOException | InterruptedException e) {
            output.append("Error executing command: ").append(e.getMessage());
        }
        return output.toString();
    }
}
