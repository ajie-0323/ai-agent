package com.donggua.aiagent.tools;

import dev.langchain4j.agent.tool.P;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

/**
 * Author: ajie
 * Date: 2026-06-18 10:30
 * Description: 基于 jsoup 抓取网页信息
 */
public class WebScrapingTool {

    @Tool(description = "Scrape the content of a page")
    public String scrapeWebPage(@ToolParam(description = "URL of the web page to scrape") String url) {
        try {
            Document elements = Jsoup.connect(url).get();
            return elements.toString();
        } catch (Exception e) {
            return "Error scrape web page:" + e.getMessage();
        }
    }
}
