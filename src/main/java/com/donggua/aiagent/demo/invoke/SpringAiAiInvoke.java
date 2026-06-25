package com.donggua.aiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Author: ajie
 * Date: 2026-06-01 14:27
 * Description: SpringAI框架调用AI 大模型
 */
// @Component
public class SpringAiAiInvoke implements CommandLineRunner {

    /**
     * spring通过名称注入bean
     */
    @Resource
    private ChatModel dashscopeChatModel;

    /**
     * 实现run方法即可在项目启动的时候测试ai大模型接口
     *
     * @param args incoming main method arguments
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {

        AssistantMessage assistantMessage = dashscopeChatModel
                // 初始化一个提示词
                .call(new Prompt("你好，我是冻瓜"))
                // 拿到返回值
                .getResult()
                // 返回数据
                .getOutput();
        // 获取返回结果
        System.out.print(assistantMessage.getText());
    }
}
