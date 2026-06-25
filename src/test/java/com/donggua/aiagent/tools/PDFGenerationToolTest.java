package com.donggua.aiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Author: ajie
 * Date: 2026-06-18 15:52
 * Description: <描述>
 */
class PDFGenerationToolTest {

    @Test
    void generatePDF() {
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        String fileName = "test1.pdf";
        String content = "测试pdf生成工具，www.baidu.com";
        String generatePDF = pdfGenerationTool.generatePDF(fileName, content);
        Assertions.assertNotNull(generatePDF);
    }
}