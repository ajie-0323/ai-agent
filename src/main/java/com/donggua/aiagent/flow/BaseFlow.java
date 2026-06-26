package com.donggua.aiagent.flow;

import com.donggua.aiagent.agent.BaseAgent;
import com.donggua.aiagent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程基类
 * 对应 OpenManus BaseFlow，管理多步骤/多智能体的执行流程
 */
@Slf4j
@Data
public abstract class BaseFlow {

    /** 流程名称 */
    protected String name;

    /** 流程中所有智能体 */
    protected List<BaseAgent> agents;

    /** 主智能体 */
    protected BaseAgent primaryAgent;

    /** 流程状态 */
    protected AgentState state = AgentState.IDLE;

    /** 当前步骤 */
    protected int currentStep = 0;

    /** 最大步骤数 */
    protected int maxSteps = 50;

    /** 执行结果 */
    protected List<String> results = new ArrayList<>();

    public BaseFlow() {
    }

    public BaseFlow(BaseAgent primaryAgent) {
        this.primaryAgent = primaryAgent;
        this.agents = new ArrayList<>();
        this.agents.add(primaryAgent);
        this.name = primaryAgent.getName() + "Flow";
    }

    public BaseFlow(List<BaseAgent> agents, BaseAgent primaryAgent) {
        this.agents = agents;
        this.primaryAgent = primaryAgent;
        this.name = primaryAgent.getName() + "Flow";
    }

    /**
     * 执行流程
     *
     * @param input 用户输入
     * @return 执行结果
     */
    public abstract String execute(String input);

    /**
     * 获取指定智能体
     */
    public BaseAgent getAgent(int index) {
        if (index >= 0 && index < agents.size()) {
            return agents.get(index);
        }
        return null;
    }

    /**
     * 添加智能体
     */
    public void addAgent(BaseAgent agent) {
        this.agents.add(agent);
    }

    /**
     * 清理资源
     */
    protected void cleanup() {
        results.clear();
        state = AgentState.IDLE;
        currentStep = 0;
    }
}
