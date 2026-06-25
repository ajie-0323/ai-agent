package com.donggua.aiagent.rag;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

/**
 * Author: ajie
 * Date: 2026-06-12 15:42
 * Description: 创建上下文查询增强器工厂
 */
public class LoveAppContextualQueryAugmenterFactory {

    public static ContextualQueryAugmenter createInstance() {
        // 创建自定义prompt模板
        PromptTemplate promptTemplate = new PromptTemplate("""
                你应该输入下面的内容：
                抱歉，我只能回答恋爱相关的问题，别的问题没有办法回答你哦，
                有问题可以联系冬瓜客服
                """);

        return ContextualQueryAugmenter.builder()
                // 设置false才能返回为空也返回
                .allowEmptyContext(false)
                // 设置默认的提示词模板
                .emptyContextPromptTemplate(promptTemplate)
                .build();
    }

}
