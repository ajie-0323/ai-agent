package com.donggua.aiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Author: ajie
 * Date: 2026-06-18 10:45
 * Description: <描述>
 */
class WebScrapingToolTest {

    @Test
    void scrapeWebPage() {
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        String url = "http://www.baidu.com";
        String webPage = webScrapingTool.scrapeWebPage(url);
        System.out.println("webPage = " + webPage);

    }
}