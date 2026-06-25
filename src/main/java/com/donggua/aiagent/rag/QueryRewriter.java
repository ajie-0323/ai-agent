package com.donggua.aiagent.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.stereotype.Component;

/**
 * Author: ajie
 * Date: 2026-06-12 14:38
 * Description: 查询重写器
 */
@Component
public class QueryRewriter {

    private final QueryTransformer queryTransformer;

    public QueryRewriter(ChatModel dashScopeChatModel) {

        ChatClient.Builder builder = ChatClient.builder(dashScopeChatModel);

        queryTransformer = RewriteQueryTransformer
                .builder()
                .chatClientBuilder(builder)
                .build();
    }

    /**
     * 执行查询重写
     *
     * @param prompt
     * @return
     */
    public String doQueryRewriter(String prompt) {
        // 创建Query对象
        Query query = new Query(prompt);
        // 重写用户提示词
        Query transform = queryTransformer.transform(query);
        // 返回重写过后的提示词
        return transform.text();
    }
}
