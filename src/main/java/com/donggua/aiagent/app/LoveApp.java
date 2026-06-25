package com.donggua.aiagent.app;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.donggua.aiagent.advisor.ForbiddenWordAdvisor;
import com.donggua.aiagent.advisor.MyLoggerAdvisor;
import com.donggua.aiagent.chatmemory.FileBasesChatMemory;
import com.donggua.aiagent.demo.invoke.TestApiKey;
import com.donggua.aiagent.rag.LoveAppRagCustomAdvisorFactory;
import com.donggua.aiagent.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Author: ajie
 * Date: 2026-06-02 15:10
 * Description: 恋爱大师app
 */
// 初始化bean
@Component
// 日志记录
@Slf4j
public class LoveApp {

    // 初始化一个chatClient
    private final ChatClient chatClient;

    // 初始话一个系统提示词
    private static final String SYSTEM_PROMPT = "你是【恋爱大师】高级情感指导师，擅长通过引导式提问深度分析恋爱问题。\n" +
            "你的工作方式：\n" +
            "1. 不直接给答案，先理解情绪。\n" +
            "2. 每次追问 2～3 个关键问题，帮助你完整掌握情况。\n" +
            "3. 追问方向包括：\n" +
            "   - 你们现在是什么关系？\n" +
            "   - 这件事发生的具体场景？\n" +
            "   - 对方最近有哪些行为让你困惑？\n" +
            "   - 你最在意的是什么？\n" +
            "   - 你希望达到什么结果？\n" +
            "4. 掌握信息后，给出清晰、可执行、不油腻的建议。\n" +
            "5. 全程温柔、耐心、尊重、安全。\n" +
            "\n" +
            "禁止：套路、PUA、毒鸡汤、越界建议。";

    @Resource
    private VectorStore loveAppVectorStore;

    /**
     * 创建ChatClient
     *
     * @param dashscopeChatModel 直接通过名称选择需要调用哪个大模型
     */
    public LoveApp(ChatModel dashscopeChatModel) {

        // 初始化基于文件的对话记忆
        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        ChatMemory chatMemory = new FileBasesChatMemory(fileDir);

        // 初始话基于内存的对话记忆
        // ChatMemory chatMemory = MessageWindowChatMemory.builder()
        //         .chatMemoryRepository(new InMemoryChatMemoryRepository())
        //         .maxMessages(10)
        //         .build();

        // 创建chatClient 并且给出调用哪个大模型 给出默认提示词和默认的拦截器
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        // 自定义违禁词Advisor，判断用户输入的是否包含违禁词
                        // new ForbiddenWordAdvisor(),
                        // 自定义日志Advisor，记录日志
                        new MyLoggerAdvisor()
                        // 自定义增强Advisor，增强大模型的识别能力
                        // ,new ReReadingAdvisor()

                )
                .build();
    }

    /**
     * AI基础对话 (支持多轮对话记忆)
     *
     * @param message 用户输入的数据
     * @param chatId  不同的id做不同的数据隔离
     * @return
     */
    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        // 可以获取到用了多少token
        // Usage usage = chatResponse.getMetadata().getUsage();
        log.info("content:{}", content);
        // log.info("usage:{}", usage.toString());
        return content;
    }

    /**
     * 流式返回 AI基础对话 (支持多轮对话记忆)
     *
     * @param message 用户输入的数据
     * @param chatId  不同的id做不同的数据隔离
     * @return
     */
    public Flux<String> doChatByStream(String message, String chatId) {
        Flux<String> flux = chatClient
                .prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
        return flux;
    }

    /**
     * JDK14 新特性 可以直接创建class类
     *
     * @param title       报告的标题
     * @param suggestions 给出的建议
     */
    record LoveReport(String title, List<String> suggestions) {

    }

    /**
     * AI恋爱报告功能 (实战结构化输出)
     *
     * @param message 用户输入的数据
     * @param chatId  不同的id做不同的数据隔离
     * @return
     */
    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient
                .prompt()
                // 给提示词怎加报告功能的提示词
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果,标题为{用户名}的恋爱报告,内容为建议列表")
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                //通过结构化输出返回自定义类格式的数据
                .entity(LoveReport.class);
        log.info("loveReport:{}", loveReport);
        return loveReport;
    }


    /**
     * AI多模态识别图片   阿里提供方法
     *
     * @return
     */
    // public static Object simpleMultiModalConversationCall(String filePath, String messages) throws ApiException, NoApiKeyException, UploadFileException {
    //     MultiModalConversation conv = new MultiModalConversation();
    //     MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
    //             .content(Arrays.asList(
    //                     Collections.singletonMap("image", filePath),
    //                     Collections.singletonMap("text", messages))).build();
    //     MultiModalConversationParam param = MultiModalConversationParam.builder()
    //             .apiKey(TestApiKey.API_KEY)
    //             .model("qwen3.7-plus")
    //             .messages(Arrays.asList(userMessage))
    //             .build();
    //     Object result = conv.call(param).getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text");
    //     return result;
    // }

    /**
     * 下面开发恋爱大师对话应用 RAG 相关功能
     */

    @Resource
    private QueryRewriter queryRewriter;

    /**
     * 和本地RAG知识库进行对话
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRag(String message, String chatId) {
        // 查询重写
        String rewriteMessage = queryRewriter.doQueryRewriter(message);
        ChatResponse chatResponse = chatClient
                .prompt()
                //使用重写后的用户信息
                .user(rewriteMessage)
                // .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                // 应用RAG知识库问答
                .advisors(QuestionAnswerAdvisor.builder(loveAppVectorStore).build())
                // 使用自定义RAG检索增强工厂 （文档查询器 + 上下文查询增强）
                // .advisors(
                //         new LoveAppRagCustomAdvisorFactory()
                //                 .createLoveAppRagCustomAdvisor(loveAppVectorStore, "单身")
                // )
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content:{}", content);
        return content;

    }

    @Resource
    private Advisor loveAppRagCloudAdvisor;

    // @Resource
    // private VectorStore pgVectorVectorStore;

    /**
     * 和阿里云服务上的RAG知识库进行对话
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRagCloud(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                // 应用RAG知识库问答
                .advisors(loveAppRagCloudAdvisor)
                // 基于pgVector 向量存储查询
                // .advisors(QuestionAnswerAdvisor.builder(pgVectorVectorStore).build())
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content:{}", content);
        return content;

    }

    @Resource
    private ToolCallback[] allTools;

    /**
     * Ai 调用工具
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithTools(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .toolCallbacks(allTools)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content:{}", content);
        return content;

    }
}
