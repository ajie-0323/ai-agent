package com.donggua.aiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Author: ajie
 * Date: 2026-06-12 13:16
 * Description: 基于ai的生辰文档元信息增强器
 */
@Component
public class MyKetWordEnricher {

    @Resource
    private ChatModel dashScopeChatModel;

    public List<Document> enricherDocument(List<Document> documents) {
        // 创建基于大模型帮助生成文档元信息增强器
        KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(dashScopeChatModel, 5);
        // 通过apply方法将文档传入 返回添加过元信息的文档列表
        return keywordMetadataEnricher.apply(documents);
    }
}
