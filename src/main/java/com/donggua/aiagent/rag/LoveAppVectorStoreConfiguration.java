package com.donggua.aiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.print.Doc;
import java.util.List;

/**
 * Author: ajie
 * Date: 2026-06-05 11:56
 * Description: 恋爱大师向量数据库配置 （基于内存的向量数据库Bean）
 */
@Configuration
public class LoveAppVectorStoreConfiguration {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;

    @Resource
    private MyKetWordEnricher myKetWordEnricher;

    @Bean
    VectorStore loveAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        // 初始化基于内存的向量数据库
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        // 读取文件
        List<Document> documentList = loveAppDocumentLoader.loadMarkdown();
        // 使用自定义切词器切分 会把原有的切好的文档切的乱七八糟
        // List<Document> splitDocuments = myTokenTextSplitter.splitCustomized(documentList);

        // 基于ai将文档中增加元信息数据
        List<Document> enricherDocuments = myKetWordEnricher.enricherDocument(documentList);
        // 存入向量数据库
        simpleVectorStore.add(enricherDocuments);
        return simpleVectorStore;
    }

}
