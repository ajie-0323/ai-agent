package com.donggua.aiagent.rag;

import com.alibaba.cloud.ai.advisor.DocumentRetrievalAdvisor;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import kotlin.contracts.Returns;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author: ajie
 * Date: 2026-06-05 15:58
 * Description: 基于阿里云知识库服务器配置的RAG增强顾问
 */
@Configuration
@Slf4j
public class LoveAppRagCloudAdvisorConfiguration {

    // 阿里云密钥
    @Value("${spring.ai.dashscope.api-key}")
    private String dashscopeApiKey;

    @Bean
    public Advisor loveAppRagCloudAdvisor() {
        // 初始化一个灵积大模型的api对象
        DashScopeApi dashScopeApi = DashScopeApi.builder().apiKey(dashscopeApiKey).build();
        final String KNOwLEDGE_INDEX = "恋爱大师知识库";
        /**
         * 通过spring alibaba的功能创建一个文档检索对象
         * 1.传入灵积api对象
         * 2.传入灵积的构建对象参数的方法
         */
        DocumentRetriever dashScopeDocumentRetriever = new DashScopeDocumentRetriever(dashScopeApi,
                DashScopeDocumentRetrieverOptions.builder()
                        // 传入知识库名称
                        .indexName(KNOwLEDGE_INDEX)
                        .build());
        // 返回生成增强顾问对象 然后传入之前创建的文档检索对象
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(dashScopeDocumentRetriever)
                .build();
    }
}
