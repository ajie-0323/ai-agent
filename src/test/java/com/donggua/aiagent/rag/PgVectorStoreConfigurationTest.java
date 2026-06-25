package com.donggua.aiagent.rag;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Author: ajie
 * Date: 2026-06-09 16:26
 * Description: <描述>
 */
@SpringBootTest
class PgVectorStoreConfigurationTest {

    @Resource
    private VectorStore pgVectorVectorStore;

    @Test
    void pgVectorVectorStore() {

        // List<Document> documents = List.of(
        //         new Document("冬瓜爱养鱼，家里有两个鱼缸", Map.of("meta1", "meta1")),
        //         new Document("彩虹魔鬼,超级红魔鬼,超级黄魔鬼并称魔鬼鱼三剑客"),
        //         new Document("冬瓜不是瓜", Map.of("meta2", "meta2")));
        //
        // // 将文档添加到 PGVector
        // pgVectorVectorStore.add(documents);

        // 检索与查询相似的文档
        List<Document> results = pgVectorVectorStore.similaritySearch(SearchRequest.builder().query("我和我的另一把关系不太好，要怎么办").topK(3).build());
        Assertions.assertNotNull(results);

    }
}