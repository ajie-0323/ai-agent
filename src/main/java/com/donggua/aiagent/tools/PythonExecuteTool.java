package com.donggua.aiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.donggua.aiagent.constant.FileConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Python 代码执行工具
 * 对应 OpenManus PythonExecute，可动态执行 Python 代码
 */
@Slf4j
public class PythonExecuteTool {

    private static final String PYTHON_SAVE_DIR = FileConstant.FILE_SAVE_DIR + "/python";

    @Tool(description = """
            Execute Python code and return the output.
            The code is saved to a temporary file and executed.
            Supports printing output with print().
            Example: print("Hello, World!")
            """)
    public String executePythonCode(
            @ToolParam(description = "Python code to execute") String code,
            @ToolParam(description = "Optional timeout in seconds (default: 30)") Integer timeoutSec) {

        if (timeoutSec == null || timeoutSec <= 0) {
            timeoutSec = 30;
        }

        FileUtil.mkdir(PYTHON_SAVE_DIR);
        String fileName = "tmp_" + System.currentTimeMillis() + ".py";
        String filePath = PYTHON_SAVE_DIR + "/" + fileName;

        try {
            // 写入代码到临时文件
            FileUtil.writeString(code, filePath, StandardCharsets.UTF_8);

            // 执行 Python
            ProcessBuilder pb = new ProcessBuilder("python", filePath);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // 等待完成
            boolean finished = process.waitFor(timeoutSec, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return "执行超时（" + timeoutSec + "秒），进程已终止";
            }

            // 读取输出
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.exitValue();

            // 清理临时文件
            FileUtil.del(filePath);

            if (exitCode != 0) {
                return "执行失败（退出码: " + exitCode + "）:\n" + output;
            }

            String result = output.toString().trim();
            return result.isEmpty() ? "代码执行成功，无输出" : result;

        } catch (IOException e) {
            log.error("Python执行IO异常", e);
            return "执行错误：" + e.getMessage() + "\n提示：请确保 Python 已安装且在 PATH 中";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "执行被中断";
        } catch (Exception e) {
            log.error("Python执行异常", e);
            return "执行错误：" + e.getMessage();
        } finally {
            FileUtil.del(filePath);
        }
    }

    @Tool(description = """
            Install a Python package using pip.
            Example: requests, numpy, pandas
            """)
    public String installPythonPackage(
            @ToolParam(description = "Package name to install (e.g., requests, numpy)") String packageName) {

        try {
            ProcessBuilder pb = new ProcessBuilder("python", "-m", "pip", "install", packageName);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            boolean finished = process.waitFor(120, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return "安装超时";
            }

            if (process.exitValue() == 0) {
                return "包安装成功: " + packageName;
            } else {
                return "安装失败:\n" + output;
            }

        } catch (Exception e) {
            return "安装错误：" + e.getMessage();
        }
    }
}
