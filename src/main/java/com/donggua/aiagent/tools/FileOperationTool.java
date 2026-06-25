package com.donggua.aiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.donggua.aiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.List;

/**
 * Author: ajie
 * Date: 2026-06-17 17:39
 * Description: 文件读写工具类
 */
public class FileOperationTool {

    /**
     * 文件保存路径
     */
    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";

    /**
     * 文件读取
     *
     * @param fileName
     * @return
     */
    @Tool(description = "Read content form a file")
    public String readFile(@ToolParam(description = "Name of a file to read") String fileName) {
        String filePath = FILE_DIR + "/" + fileName;
        try {
            return FileUtil.readUtf8String(filePath);
        } catch (Exception e) {
            return "Error reading file :" + e.getMessage();
        }
    }

    /**
     * 文件写入
     *
     * @param fileName
     * @param content
     * @return
     */
    @Tool(description = "Write content to a file")
    public String writeFile(@ToolParam(description = "Name of a file to write") String fileName,
                            @ToolParam(description = "Content t write to the file") String content) {
        String filePath = FILE_DIR + "/" + fileName;

        try {
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeUtf8String(content, filePath);
            return "File written successfully to:" + filePath;
        } catch (Exception e) {
            return "Error writing file:" + e.getMessage();
        }

    }
}
