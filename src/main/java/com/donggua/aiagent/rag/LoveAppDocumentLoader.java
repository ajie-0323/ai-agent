package com.donggua.aiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: ajie
 * Date: 2026-06-04 16:43
 * Description: 自定义转换Markdown文件加载器
 */
@Component
@Slf4j
public class LoveAppDocumentLoader {

    // 创建一个文件转换加载器
    private final ResourcePatternResolver resourcePatternResolver;

    public LoveAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    /**
     * 加载所有的markdown文件
     *
     * @return
     */
    public List<Document> loadMarkdown() {
        List<Document> allDocument = new ArrayList<>();

        try {
            // 从根目录下的document文件中读取所有的md文件
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");
            // 循环文件
            for (Resource resource : resources) {
                // 获取文件名称
                String filename = resource.getFilename();
                // 通过文件名称获取到状态
                String status = filename.substring(filename.length() - 6, filename.length() - 4);
                // md文件读取配置
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .withAdditionalMetadata("filename", filename)
                        // 增加各种的元信息
                        .withAdditionalMetadata("status", status)
                        .build();
                // 生成读取md文件对象

                MarkdownDocumentReader markdownDocumentReader = new MarkdownDocumentReader(resource, config);
                // 将数据添加到所有文件集合中
                allDocument.addAll(markdownDocumentReader.get());
            }
        } catch (IOException e) {
            log.error("Markdown文件读取失败", e);
        }
        return allDocument;
    }


}
