package com.donggua.aiagent.demo.invoke;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * 阿里云灵积 Http AI调用
 */
public class HttpAiInvoke {

    public static void main(String[] args) {
        String apiKey = TestApiKey.API_KEY; // 替换为实际的 API Key

        // 构建请求体
        JSONObject requestBody = new JSONObject();
        requestBody.set("model", "qwen-plus");

        // 构建 messages
        JSONArray messages = new JSONArray();

        JSONObject systemMessage = new JSONObject();
        systemMessage.set("role", "system");
        systemMessage.set("content", "You are a helpful assistant.");
        messages.add(systemMessage);

        JSONObject userMessage = new JSONObject();
        userMessage.set("role", "user");
        userMessage.set("content", "你是谁？");
        messages.add(userMessage);

        // 设置 input
        JSONObject input = new JSONObject();
        input.set("messages", messages);
        requestBody.set("input", input);

        // 设置 parameters
        JSONObject parameters = new JSONObject();
        parameters.set("result_format", "message");
        requestBody.set("parameters", parameters);

        // 发送请求
        HttpResponse response = HttpRequest.post("https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", ContentType.JSON.toString())
                .body(requestBody.toString())
                .execute();

        // 处理响应
        if (response.isOk()) {
            String responseBody = response.body();
            System.out.println("响应结果: " + responseBody);

            // 解析响应
            JSONObject jsonResponse = JSONUtil.parseObj(responseBody);
            System.out.println("格式化输出: " + jsonResponse.toStringPretty());
        } else {
            System.err.println("请求失败，状态码: " + response.getStatus());
            System.err.println("错误信息: " + response.body());
        }
    }
}
