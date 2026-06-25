package com.donggua.aiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

/**
 * Author: ajie
 * Date: 2026-06-09 16:14
 * Description: PgVectorStore的配置类
 */
@Configuration
public class PgVectorStoreConfiguration {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    // @Bean
    // public VectorStore pgVectorVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel dasScopeEmbeddingModel) {
    //     PgVectorStore pgVectorStore = PgVectorStore.builder(jdbcTemplate, dasScopeEmbeddingModel)
    //             .dimensions(1024)                    // 可选：默认为模型维度或 1536
    //             .distanceType(COSINE_DISTANCE)       // 可选：默认为 COSINE_DISTANCE
    //             .indexType(HNSW)                     // 可选：默认为 HNSW
    //             .initializeSchema(true)              // 可选：默认为 false
    //             .schemaName("public")                // 可选：默认为 "public"
    //             .vectorTableName("vector_store1")     // 可选：默认为 "vector_store"
    //             .maxDocumentBatchSize(10000)         // 可选：默认为 10000
    //             .build();
    //
    //     // 读取之前的文档 把文档信息存到vectorStore里面
    //     // pgVectorStore.add(loveAppDocumentLoader.loadMarkdown());
    //
    //     // 读取所有文档片段
    //     List<Document> allDocuments = loveAppDocumentLoader.loadMarkdown();
    //
    //     // 因为dashScopeChatModel一次只能处理10条数据
    //     int batchSize = 10;
    //     for (int i = 0; i < allDocuments.size(); i += batchSize) {
    //         int end = Math.min(i + batchSize, allDocuments.size());
    //         List<Document> batch = allDocuments.subList(i, end);
    //         pgVectorStore.add(batch);
    //         System.out.println("已添加第 " + (i / batchSize + 1) + " 批，共 " + batch.size() + " 个文档片段");
    //     }
    //
    //     return pgVectorStore;
    // }
}
