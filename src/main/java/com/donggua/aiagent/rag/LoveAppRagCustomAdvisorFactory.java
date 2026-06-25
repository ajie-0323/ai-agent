package com.donggua.aiagent.rag;

import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * Author: ajie
 * Date: 2026-06-12 15:03
 * Description:创建自定义的RAG检索增强工厂
 */
public class LoveAppRagCustomAdvisorFactory {

    /**
     * 创建自定义的RAG的检索增强顾问
     *
     * @param vectorStore
     * @param status
     * @return
     */
    public static Advisor createLoveAppRagCustomAdvisor(VectorStore vectorStore, String status) {
        Filter.Expression expression = new FilterExpressionBuilder()
                .eq("status", status)
                .build();

        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever
                .builder()
                .vectorStore(vectorStore)       // 指定对应的向量存储
                .filterExpression(expression)   // 指定过滤条件
                .similarityThreshold(0.5)       // 返回阈值
                .topK(3)                        // 返回文档数
                .build();

        return RetrievalAugmentationAdvisor
                .builder()
                .documentRetriever(documentRetriever)
                .queryAugmenter(
                        LoveAppContextualQueryAugmenterFactory.createInstance()
                )
                .build();
    }
}
