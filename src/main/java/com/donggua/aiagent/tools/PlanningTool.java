package com.donggua.aiagent.tools;

import cn.hutool.core.date.DateUtil;
import com.donggua.aiagent.tools.model.ToolResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 任务规划工具
 * 对应 OpenManus PlanningTool，支持创建、更新、追踪任务计划
 */
@Slf4j
public class PlanningTool {

    /** 存储所有计划 */
    private static final Map<String, Plan> PLANS = new ConcurrentHashMap<>();

    // ==================== 内部数据结构 ====================

    @Data
    public static class Plan {
        private String planId;
        private String title;
        private List<PlanStep> steps;
        private String createdAt;
        private String updatedAt;
        private PlanStatus status;

        public Plan(String planId, String title, List<PlanStep> steps) {
            this.planId = planId;
            this.title = title;
            this.steps = steps;
            this.createdAt = DateUtil.now();
            this.updatedAt = DateUtil.now();
            this.status = PlanStatus.NOT_STARTED;
        }

        /** 获取活跃状态步骤（未开始或进行中） */
        public List<PlanStep> getActiveSteps() {
            return steps.stream()
                    .filter(s -> s.getStatus() == PlanStepStatus.NOT_STARTED
                            || s.getStatus() == PlanStepStatus.IN_PROGRESS)
                    .collect(Collectors.toList());
        }

        /** 检查是否全部完成 */
        public boolean isAllCompleted() {
            return steps.stream().allMatch(s -> s.getStatus() == PlanStepStatus.COMPLETED);
        }

        /** 获取完成进度 */
        public String getProgress() {
            long completed = steps.stream().filter(s -> s.getStatus() == PlanStepStatus.COMPLETED).count();
            return completed + "/" + steps.size();
        }
    }

    @Data
    public static class PlanStep {
        private int stepIndex;
        private String description;
        private PlanStepStatus status;
        private String result;

        public PlanStep(int index, String description) {
            this.stepIndex = index;
            this.description = description;
            this.status = PlanStepStatus.NOT_STARTED;
        }
    }

    public enum PlanStatus {
        NOT_STARTED, IN_PROGRESS, COMPLETED, BLOCKED
    }

    public enum PlanStepStatus {
        NOT_STARTED("⏳"),
        IN_PROGRESS("🔄"),
        COMPLETED("✅"),
        BLOCKED("❌");

        private final String marker;

        PlanStepStatus(String marker) {
            this.marker = marker;
        }

        public String getMarker() {
            return marker;
        }
    }

    // ==================== Tools ====================

    @Tool(description = """
            Create a plan for solving a complex task. The plan should include a title and a list of steps.
            Each step should be a clear, actionable description of what needs to be done.
            Returns the plan ID for future reference.
            """)
    public String createPlan(
            @ToolParam(description = "Unique identifier for this plan") String planId,
            @ToolParam(description = "Title of the plan") String title,
            @ToolParam(description = "List of step descriptions in order, e.g. [\"Step 1: ...\", \"Step 2: ...\"]") List<String> steps) {

        if (PLANS.containsKey(planId)) {
            return "计划已存在，请使用 updatePlan 更新或使用其他 planId";
        }

        List<PlanStep> planSteps = new ArrayList<>();
        for (int i = 0; i < steps.size(); i++) {
            planSteps.add(new PlanStep(i + 1, steps.get(i)));
        }

        Plan plan = new Plan(planId, title, planSteps);
        PLANS.put(planId, plan);

        log.info("创建计划: {} - {} ({}步)", planId, title, steps.size());
        return formatPlan(plan);
    }

    @Tool(description = "Update the status of a specific step in a plan")
    public String updatePlanStep(
            @ToolParam(description = "Plan ID") String planId,
            @ToolParam(description = "Step number (1-based) to update") int stepIndex,
            @ToolParam(description = "New status: not_started, in_progress, completed, blocked") String status,
            @ToolParam(description = "Optional result/notes for this step") String result) {

        Plan plan = PLANS.get(planId);
        if (plan == null) {
            return "计划不存在: " + planId;
        }

        if (stepIndex < 1 || stepIndex > plan.getSteps().size()) {
            return "步骤序号无效: " + stepIndex + "，有效范围 1-" + plan.getSteps().size();
        }

        PlanStep step = plan.getSteps().get(stepIndex - 1);
        try {
            step.setStatus(PlanStepStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return "无效状态: " + status + "，可选: not_started, in_progress, completed, blocked";
        }
        if (result != null && !result.isBlank()) {
            step.setResult(result);
        }

        plan.setUpdatedAt(DateUtil.now());

        // 自动更新计划状态
        if (plan.isAllCompleted()) {
            plan.setStatus(PlanStatus.COMPLETED);
        } else if (plan.getStatus() == PlanStatus.NOT_STARTED) {
            plan.setStatus(PlanStatus.IN_PROGRESS);
        }

        log.info("更新计划 {} 步骤 {} -> {}", planId, stepIndex, status);
        return formatPlan(plan);
    }

    @Tool(description = "Get the current status and details of a plan")
    public String getPlan(
            @ToolParam(description = "Plan ID to retrieve") String planId) {

        Plan plan = PLANS.get(planId);
        if (plan == null) {
            return "计划不存在: " + planId;
        }
        return formatPlan(plan);
    }

    @Tool(description = "List all existing plans and their statuses")
    public String listPlans() {
        if (PLANS.isEmpty()) {
            return "当前没有计划";
        }

        StringBuilder sb = new StringBuilder("📋 计划列表：\n");
        for (Map.Entry<String, Plan> entry : PLANS.entrySet()) {
            Plan plan = entry.getValue();
            sb.append("  - ").append(plan.getPlanId())
                    .append(": ").append(plan.getTitle())
                    .append(" [").append(plan.getStatus()).append("]")
                    .append(" (").append(plan.getProgress()).append(")")
                    .append("\n");
        }
        return sb.toString();
    }

    @Tool(description = "Delete an existing plan")
    public String deletePlan(
            @ToolParam(description = "Plan ID to delete") String planId) {

        Plan removed = PLANS.remove(planId);
        if (removed != null) {
            log.info("删除计划: {}", planId);
            return "计划已删除: " + planId;
        }
        return "计划不存在: " + planId;
    }

    // ==================== 内部方法 ====================

    private String formatPlan(Plan plan) {
        StringBuilder sb = new StringBuilder();
        sb.append("📋 计划: ").append(plan.getTitle()).append("\n");
        sb.append("  ID: ").append(plan.getPlanId()).append("\n");
        sb.append("  状态: ").append(plan.getStatus()).append("\n");
        sb.append("  进度: ").append(plan.getProgress()).append("\n");
        sb.append("  步骤:\n");

        for (PlanStep step : plan.getSteps()) {
            sb.append("    ").append(step.getStepIndex()).append(". ")
                    .append(step.getStatus().getMarker()).append(" ")
                    .append(step.getDescription());
            if (step.getResult() != null && !step.getResult().isBlank()) {
                sb.append(" → ").append(step.getResult());
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
