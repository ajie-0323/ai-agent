package com.donggua.aiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.donggua.aiagent.constant.FileConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.nio.charset.StandardCharsets;

/**
 * 智能文件编辑工具
 * 对应 OpenManus StrReplaceEditor，支持精确查找替换、插入、编辑
 */
@Slf4j
public class StrReplaceEditorTool {

    private static final String WORK_DIR = FileConstant.FILE_SAVE_DIR + "/workspace";

    @Tool(description = """
            View the content of a file. Shows line numbers for reference.
            Use this before making edits to understand the current content.
            """)
    public String viewFile(
            @ToolParam(description = "Path to the file to view") String filePath) {

        try {
            if (!FileUtil.isFile(filePath)) {
                return "文件不存在: " + filePath;
            }
            String content = FileUtil.readUtf8String(filePath);
            String[] lines = content.split("\n", -1);
            StringBuilder sb = new StringBuilder();
            sb.append("📄 ").append(filePath).append(" (").append(lines.length).append("行)\n");
            // 显示带行号的内容
            for (int i = 0; i < lines.length; i++) {
                sb.append(String.format("%4d | ", i + 1)).append(lines[i]).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "读取文件错误：" + e.getMessage();
        }
    }

    @Tool(description = """
            Create a new file with the given content.
            If the file already exists, it will be overwritten.
            """)
    public String createFile(
            @ToolParam(description = "Path where to create the file") String filePath,
            @ToolParam(description = "Content to write to the file") String content) {

        try {
            FileUtil.mkParentDirs(filePath);
            FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
            log.info("创建文件: {} ({} 字符)", filePath, content.length());
            return "文件创建成功: " + filePath;
        } catch (Exception e) {
            return "创建文件错误：" + e.getMessage();
        }
    }

    @Tool(description = """
            Replace all occurrences of old_str with new_str in a file.
            This is a precise string replacement - it does not use regex.
            Use viewFile first to see the exact content to replace.
            """)
    public String strReplace(
            @ToolParam(description = "Path to the file to edit") String filePath,
            @ToolParam(description = "Exact text to find (case-sensitive)") String oldStr,
            @ToolParam(description = "Text to replace with") String newStr) {

        try {
            if (!FileUtil.isFile(filePath)) {
                return "文件不存在: " + filePath;
            }

            String content = FileUtil.readUtf8String(filePath);

            if (!content.contains(oldStr)) {
                return "在文件中未找到指定文本:\n" + oldStr + "\n请使用 viewFile 查看文件实际内容";
            }

            // 统计替换次数
            int count = content.split(oldStr, -1).length - 1;
            String newContent = content.replace(oldStr, newStr);
            FileUtil.writeString(newContent, filePath, StandardCharsets.UTF_8);

            log.info("文件替换: {} (替换了 {} 处)", filePath, count);
            return "替换完成：在 " + filePath + " 中替换了 " + count + " 处";
        } catch (Exception e) {
            return "替换错误：" + e.getMessage();
        }
    }

    @Tool(description = """
            Insert text at a specific line number in a file.
            The inserted text will appear BEFORE the specified line.
            """)
    public String insertLine(
            @ToolParam(description = "Path to the file to edit") String filePath,
            @ToolParam(description = "Line number to insert before (1-based)") int lineNumber,
            @ToolParam(description = "Text to insert") String textToInsert) {

        try {
            if (!FileUtil.isFile(filePath)) {
                return "文件不存在: " + filePath;
            }

            String content = FileUtil.readUtf8String(filePath);
            String[] lines = content.split("\n", -1);

            if (lineNumber < 1 || lineNumber > lines.length + 1) {
                return "行号无效: " + lineNumber + "，有效范围 1-" + (lines.length + 1);
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < lines.length; i++) {
                if (i + 1 == lineNumber) {
                    sb.append(textToInsert).append("\n");
                }
                sb.append(lines[i]);
                if (i < lines.length - 1 || content.endsWith("\n")) {
                    sb.append("\n");
                }
            }
            // 如果插入到最后一行之后
            if (lineNumber == lines.length + 1) {
                sb.append(textToInsert).append("\n");
            }

            FileUtil.writeString(sb.toString(), filePath, StandardCharsets.UTF_8);
            log.info("文件插入: {} 第{}行", filePath, lineNumber);
            return "插入成功：在 " + filePath + " 第 " + lineNumber + " 行前插入";
        } catch (Exception e) {
            return "插入错误：" + e.getMessage();
        }
    }

    @Tool(description = """
            List files in a directory.
            Shows file names and sizes.
            """)
    public String listFiles(
            @ToolParam(description = "Directory path to list") String dirPath) {

        try {
            if (!FileUtil.isDirectory(dirPath)) {
                return "目录不存在: " + dirPath;
            }

            java.io.File dir = new java.io.File(dirPath);
            java.io.File[] files = dir.listFiles();
            if (files == null || files.length == 0) {
                return "目录为空: " + dirPath;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("📁 ").append(dirPath).append("\n");
            for (java.io.File f : files) {
                String size = f.isDirectory() ? "<DIR>" : FileUtil.readableFileSize(f);
                sb.append("  ").append(f.isDirectory() ? "📁" : "📄")
                        .append(" ").append(f.getName())
                        .append(" (").append(size).append(")\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "列出目录错误：" + e.getMessage();
        }
    }
}
