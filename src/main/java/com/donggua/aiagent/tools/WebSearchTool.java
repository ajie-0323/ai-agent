package com.donggua.aiagent.tools;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WebSearchTool {

    // SearchAPI 的搜索接口地址
    private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";

    private final String apiKey;

    public WebSearchTool(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * 通过SearchApi的调用百度查询接口
     *
     * @param query
     * @return
     */
    @Tool(description = "Search for information from Baidu Search Engine")
    public String searchWeb(
            @ToolParam(description = "Search query keyword") String query) {
        // 设置请求参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("q", query);
        paramMap.put("api_key", apiKey);
        paramMap.put("engine", "baidu");
        try {
            //获取到返回值
            String response = HttpUtil.get(SEARCH_API_URL, paramMap);
            // 取出返回结果的前 5 条
            JSONObject jsonObject = JSONUtil.parseObj(response);
            // 提取 organic_results 部分
            JSONArray organicResults = jsonObject.getJSONArray("organic_results");
            // List<Object> objects = organicResults.subList(0, 5);
            // 拼接搜索结果为字符串
            // String result = objects.stream().map(obj -> {
            //     JSONObject tmpJSONObject = (JSONObject) obj;
            //     return tmpJSONObject.toString();
            // }).collect(Collectors.joining(","));

            // 只获取结果中对应的几个字段
            JSONArray filteredResults = new JSONArray();
            int maxResults = Math.min(5, organicResults.size());

            for (int i = 0; i < maxResults; i++) {
                JSONObject item = organicResults.getJSONObject(i);
                // 创建只包含三个字段的新JSON对象
                JSONObject filteredItem = new JSONObject();
                filteredItem.put("title", item.getStr("title", ""));
                filteredItem.put("link", item.getStr("link", ""));
                filteredItem.put("snippet", item.getStr("snippet", ""));
                filteredResults.add(filteredItem);
            }

            // 返回精简后的JSON数组字符串
            return filteredResults.toString();
            // return result;
        } catch (Exception e) {
            return "Error searching Baidu: " + e.getMessage();
        }
    }
}
