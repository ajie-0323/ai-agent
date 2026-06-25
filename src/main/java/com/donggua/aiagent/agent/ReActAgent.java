package com.donggua.aiagent.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * Author: ajie
 * Date: 2026-06-24 16:22
 * Description: ReAct（Reasoning and Acting）模式的代理抽象类
 * 实现了思考-行动的循环模式
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Slf4j
public abstract class ReActAgent extends BaseAgent {

    /**
     * 处理当前状态并决定下一步行动
     *
     * @return 是否需要执行行动，true表示需要执行，false表示无需执行
     */
    public abstract boolean think();

    /**
     * 执行决定的行动
     *
     * @return 执行的结果
     */
    public abstract String act();


    /**
     * 执行单步操作，思考和行动
     *
     * @return 步骤执行结果
     */
    @Override
    public String step() {
        try {
            boolean shouldAct = think();
            if (!shouldAct) {
                return "思考完成 - 无需行动";
            }
            return act();
        } catch (Exception e) {
            e.getStackTrace();
            return "步骤执行失败：" + e.getMessage();
        }
    }
}
