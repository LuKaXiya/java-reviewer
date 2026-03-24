package com.example.javareviewer.model;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record ProjectReviewResult(Path target, List<ReviewResult> fileResults, ProjectStructureSummary structureSummary) {

    public ProjectReviewResult(Path target, List<ReviewResult> fileResults) {
        this(target, fileResults, ProjectStructureSummary.empty());
    }

    public int totalFiles() {
        return fileResults.size();
    }

    public int totalIssues() {
        return fileResults.stream().mapToInt(result -> result.issues().size()).sum();
    }

    public Map<Severity, Long> severityCounts() {
        Map<Severity, Long> counts = new EnumMap<>(Severity.class);
        for (Severity severity : Severity.values()) {
            counts.put(severity, 0L);
        }
        for (ReviewResult result : fileResults) {
            result.severityCounts().forEach((severity, count) -> counts.merge(severity, count, Long::sum));
        }
        return counts;
    }

    public List<ReviewResult> worstFiles(int limit) {
        return fileResults.stream()
                .sorted(Comparator
                        .comparingInt((ReviewResult result) -> result.issues().size()).reversed()
                        .thenComparing(result -> result.target().toString()))
                .limit(limit)
                .toList();
    }

    public List<RecommendedAction> recommendedActions(int limit) {
        Map<String, List<ReviewIssue>> grouped = fileResults.stream()
                .flatMap(result -> result.issues().stream())
                .collect(Collectors.groupingBy(ReviewIssue::ruleName, LinkedHashMap::new, Collectors.toList()));

        return grouped.entrySet().stream()
                .map(entry -> toAction(entry.getKey(), entry.getValue()))
                .sorted(Comparator
                        .comparing((RecommendedAction action) -> action.priority().weight()).reversed()
                        .thenComparing(RecommendedAction::issueCount, Comparator.reverseOrder())
                        .thenComparing(RecommendedAction::title))
                .limit(limit)
                .toList();
    }

    private RecommendedAction toAction(String ruleName, List<ReviewIssue> issues) {
        Severity priority = issues.stream().map(ReviewIssue::severity).max(Comparator.comparingInt(Severity::weight)).orElse(Severity.LOW);
        String recommendation = switch (ruleName) {
            case "ControllerDirectRepositoryDependencyRule" -> "让 Controller 只依赖 Service，并把数据访问编排下沉到 Service/UseCase。";
            case "ControllerBusinessLogicRule" -> "把循环、校验、计算、聚合等业务逻辑从 Controller 提炼到 Service。";
            case "ServiceWriteMethodWithoutTransactionalRule", "TransactionalRiskRule" -> "梳理事务边界：写操作集中在 Service，并确保 @Transactional 放在可被代理的方法/类上。";
            case "LoggingWithoutExceptionObjectRule", "PrintStackTraceRule", "ConsolePrintRule" -> "统一改为结构化日志，记录关键信息并在异常场景传入 throwable。";
            case "CatchExceptionSwallowRule", "BroadCatchExceptionRule" -> "缩小异常捕获范围，避免吞异常，必要时记录后重新抛出。";
            case "RepositoryNamingAndResponsibilityRule" -> "统一 Repository/DAO 命名，并移除持久层中的 Web/业务逻辑。";
            case "ServiceWebObjectLeakRule" -> "Service 层只处理领域参数，不直接依赖 Servlet / MVC 对象。";
            default -> "优先处理该规则对应问题，减少重复缺陷并固化为团队代码规范。";
        };
        return new RecommendedAction(priority, ruleName, issues.size(), recommendation);
    }
}
