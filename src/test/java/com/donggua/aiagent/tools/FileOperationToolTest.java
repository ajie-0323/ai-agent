package com.donggua.aiagent.tools;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Author: ajie
 * Date: 2026-06-18 8:51
 * Description: <描述>
 */
class FileOperationToolTest {

    @Test
    void readFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String fileName = "test1.txt";

        String read = fileOperationTool.readFile(fileName);
        System.out.print("read  = " + read);


    }

    @Test
    void writeFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String fileName = "test1.txt";
        String content = "你好 我是冻瓜";
        String file = fileOperationTool.writeFile(fileName, content);
        System.out.print("file = " + file);
    }
}