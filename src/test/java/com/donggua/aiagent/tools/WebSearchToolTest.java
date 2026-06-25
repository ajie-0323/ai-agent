package com.donggua.aiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Author: ajie
 * Date: 2026-06-18 10:04
 * Description: <描述>
 */
@SpringBootTest
class WebSearchToolTest {

    // 通过spring启动时加载配置文件初始化key
    @Value("${search-api.api-key}")
    private String apiKey;

    @Test
    void searchWeb() {
        System.out.println("apiKey = " + apiKey);
        WebSearchTool webSearchTool = new WebSearchTool(apiKey);
        String searchWeb = webSearchTool.searchWeb("南京天气");
        System.out.println("searchWeb = " + searchWeb);
    }
}