package com.donggua.aiagent.agent.model;

/**
 * Author: ajie
 * Date: 2026-06-24 16:19
 * Description: 执行智能体流程状态
 */
public enum AgentState {

    /**
     * 空闲状态
     */
    IDLE,

    /**
     * 运行状态
     */
    RUNNING,

    /**
     * 结束状态
     */
    FINISH,

    /**
     * 异常状态
     */
    ERROR
}
