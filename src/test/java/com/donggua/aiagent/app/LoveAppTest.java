package com.donggua.aiagent.app;

import cn.hutool.core.lang.UUID;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.esotericsoftware.minlog.Log;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Author: ajie
 * Date: 2026-06-02 15:55
 * Description: <描述>
 */
@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();

        // 第一轮对话
        String message = "你好，我是冻瓜！";
        String content = loveApp.doChat(message, chatId);
        // 第二轮对话
        message = "我想让我的另一半（517）更爱我";
        content = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(content);
        // 第三轮对话
        message = "我的另一半叫什么名字的？我几个我和你说过，帮我回忆一下";
        content = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(content);

    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是冻瓜！我想让我的另一半（517）更爱我，但我不知道该怎么做";
        // 测试违禁词Advisor 成功
        // String message = "你好，我是冻瓜！我的另一半不爱我 我想自杀";
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(loveReport);

    }

    // @Test
    // void simpleMultiModalConversationCall() {
    //     try {
    //         String model = "cooker_plate_brand";
    //         // 定义带有变量的模板
    //         String template = "你好，请帮我看一下图中的{params1}的{params2}是什么。不要返回任何多余文字，只返回JSON";
    //
    //         // 创建模板对象
    //         PromptTemplate promptTemplate = new PromptTemplate(template);
    //
    //         // 准备变量映射
    //         Map<String, Object> variables = new HashMap<>();
    //         if (model == "cooker_plate_brand") {
    //             variables.put("params1", "灶具");
    //             variables.put("params2", "安装方式");
    //         }
    //
    //         // 生成最终提示文本
    //         String prompt = promptTemplate.render(variables);
    //         System.out.print("prompt：" + prompt);
    //
    //         Object result = LoveApp.simpleMultiModalConversationCall("tmp/images/20260530165634060_KF9-97.jpg", prompt);
    //         System.out.print("result:" + result);
    //     } catch (NoApiKeyException e) {
    //         throw new RuntimeException(e);
    //     } catch (UploadFileException e) {
    //         throw new RuntimeException(e);
    //     } catch (Exception e) {
    //         throw new RuntimeException(e);
    //     }
    // }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        // String message = "我已经结婚了,但是婚后的生活不太亲密,我该怎么办";
        String message = "我周围朋友都有对象了，我感觉很不自在";
        String answer = loveApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithRagCloud() {
        String chatId = UUID.randomUUID().toString();
        String message = "我周围朋友都有对象了，我感觉很不自在";
        // String answer = loveApp.doChatWithRag(message, chatId);
        // Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
        testMessage("周末想带女朋友去上海约会，推荐几个适合情侣的小众打卡地？");

        // 测试网页抓取：恋爱案例分析
        testMessage("最近和对象吵架了，看看编程导航网站（codefather.cn）的其他情侣是怎么解决矛盾的？");

        // 测试资源下载：图片下载
        testMessage("直接下载一张适合做手机壁纸的星空情侣图片为文件");

        // 测试终端操作：执行代码
        testMessage("执行 Python3 脚本来生成数据分析报告");

        // 测试文件操作：保存用户档案
        testMessage("保存我的恋爱档案为文件");

        // 测试 PDF 生成
        testMessage("生成一份‘七夕约会计划’PDF，包含餐厅预订、活动流程和礼物清单");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = loveApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

}