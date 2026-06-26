package com.donggua.aiagent.flow;

import cn.hutool.core.util.IdUtil;
import com.donggua.aiagent.agent.BaseAgent;
import com.donggua.aiagent.agent.model.AgentState;
import com.donggua.aiagent.tools.PlanningTool;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 计划驱动流程
 * 对应 OpenManus PlanningFlow，智能体先制定计划，再按步骤执行
 * <p>
 * 工作流程：
 * 1. 智能体分析任务，创建步骤计划
 * 2. 按步骤逐一执行
 * 3. 每一步完成后更新计划状态
 * 4. 全部完成或出错时结束
 */
@Slf4j
public class PlanningFlow extends BaseFlow {

    private final PlanningTool planningTool;
    private String currentPlanId;

    public PlanningFlow(BaseAgent agent) {
        super(agent);
        this.planningTool = new PlanningTool();
    }

    @Override
    public String execute(String input) {
        this.state = AgentState.RUNNING;
        results.clear();

        try {
            // 第一步：让主智能体分析任务并制定计划
            log.info("{} | 开始分析任务...", name);
            String planPrompt = buildPlanPrompt(input);
            String planResult = primaryAgent.run(planPrompt);
            results.add("【任务分析】\n" + planResult);

            // 第二步：从分析结果中提取步骤，创建计划
            String defaultPlanId = "plan_" + IdUtil.fastSimpleUUID();
            this.currentPlanId = defaultPlanId;

            List<String> steps = extractStepsFromResult(planResult);
            if (!steps.isEmpty()) {
                String title = "任务计划: " + (input.length() > 50 ? input.substring(0, 50) + "..." : input);
                String planCreated = planningTool.createPlan(defaultPlanId, title, steps);
                results.add("【创建计划】\n" + planCreated);
            }

            // 第三步：按计划执行
            String planStatus = planningTool.getPlan(currentPlanId);
            if (planStatus.contains("步骤")) {
                results.add(executeByPlan(currentPlanId, input));
            } else {
                // 没有计划直接执行
                results.add("【直接执行】\n" + primaryAgent.run(input));
            }

            this.state = AgentState.FINISH;
            return String.join("\n\n", results);

        } catch (Exception e) {
            this.state = AgentState.ERROR;
            log.error("流程执行错误", e);
            return "流程执行错误：" + e.getMessage();
        } finally {
            cleanup();
        }
    }

    /**
     * 按计划逐步执行
     */
    private String executeByPlan(String planId, String originalInput) {
        StringBuilder sb = new StringBuilder("【按计划执行】\n");
        String planDetail = planningTool.getPlan(planId);

        // 从计划详情中提取总步骤数
        int totalSteps = countSteps(planDetail);

        for (int i = 0; i < totalSteps && currentStep < maxSteps; i++) {
            currentStep++;
            int stepIndex = i + 1;

            // 更新步骤状态为进行中
            planningTool.updatePlanStep(planId, stepIndex, "in_progress", null);
            String stepInfo = getStepDescription(planDetail, stepIndex);

            log.info("执行步骤 {}/{}: {}", stepIndex, totalSteps, stepInfo);
            sb.append("\n步骤 ").append(stepIndex).append("/").append(totalSteps)
                    .append(": ").append(stepInfo).append("\n");

            try {
                // 让智能体执行当前步骤
                String stepPrompt = buildStepPrompt(originalInput, stepIndex, totalSteps, stepInfo);
                String stepResult = primaryAgent.run(stepPrompt);
                String truncated = truncateResult(stepResult);
                sb.append("结果: ").append(truncated).append("\n");

                // 更新计划
                planningTool.updatePlanStep(planId, stepIndex, "completed", truncated);
            } catch (Exception e) {
                log.error("步骤 {} 执行失败", stepIndex, e);
                planningTool.updatePlanStep(planId, stepIndex, "blocked",
                        "错误: " + e.getMessage());
                sb.append("失败: ").append(e.getMessage()).append("\n");
            }
        }

        sb.append("\n").append(planningTool.getPlan(planId));
        return sb.toString();
    }

    /**
     * 构建计划提示词
     */
    private String buildPlanPrompt(String userInput) {
        return """
                你需要分析以下任务，并制定一个详细的执行计划。

                任务: %s

                请分析这个任务需要哪些步骤，并给出一个清晰的计划。
                每个步骤应该是一个具体的、可执行的操作描述。
                如果你需要使用工具，也要在步骤中说明需要使用什么工具。

                请按以下格式回复：
                1. 任务分析：简要分析任务需求
                2. 执行计划：列出具体步骤
                3. 需要的工具：列出可能需要用到的工具
                """.formatted(userInput);
    }

    /**
     * 构建步骤执行提示词
     */
    private String buildStepPrompt(String originalInput, int stepIndex, int totalSteps, String stepDesc) {
        return """
                原始任务: %s

                当前步骤 (%d/%d): %s

                请专注于执行当前步骤，使用可用的工具完成任务。
                执行完成后，汇报该步骤的结果。
                """.formatted(originalInput, stepIndex, totalSteps, stepDesc);
    }

    /**
     * 从结果中提取步骤
     */
    private List<String> extractStepsFromResult(String result) {
        Pattern pattern = Pattern.compile("\\d+\\.\\s*(.+)");
        Matcher matcher = pattern.matcher(result);
        java.util.List<String> steps = new java.util.ArrayList<>();
        while (matcher.find()) {
            String step = matcher.group(1).trim();
            if (!step.isEmpty() && !step.startsWith("需要") && !step.startsWith("任务")) {
                steps.add(step);
            }
        }
        return steps;
    }

    /**
     * 从计划详情中统计步骤数
     */
    private int countSteps(String planDetail) {
        int count = 0;
        for (String line : planDetail.split("\n")) {
            if (line.matches("\\s*\\d+\\..*")) {
                count++;
            }
        }
        return count;
    }

    /**
     * 从计划详情中获取指定步骤的描述
     */
    private String getStepDescription(String planDetail, int stepIndex) {
        int current = 0;
        for (String line : planDetail.split("\n")) {
            if (line.matches("\\s*" + stepIndex + "\\..*")) {
                return line.trim();
            }
        }
        return "步骤 " + stepIndex;
    }

    /**
     * 截断结果
     */
    private String truncateResult(String result) {
        if (result != null && result.length() > 200) {
            return result.substring(0, 200) + "...";
        }
        return result;
    }
}
