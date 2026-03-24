package com.example.javareviewer.report;

import com.example.javareviewer.model.ProjectReviewResult;
import com.example.javareviewer.model.RecommendedAction;
import com.example.javareviewer.model.ReviewIssue;
import com.example.javareviewer.model.ReviewResult;
import com.example.javareviewer.model.Severity;

public class ConsoleReportRenderer implements ReportRenderer {

    @Override
    public String render(ReviewResult result) {
        StringBuilder builder = new StringBuilder();
        builder.append("Java Reviewer Report").append(System.lineSeparator());
        appendSingleFile(builder, result);
        return builder.toString();
    }

    @Override
    public String render(ProjectReviewResult result) {
        StringBuilder builder = new StringBuilder();
        builder.append("Java Reviewer Project Report").append(System.lineSeparator());
        builder.append("Target directory: ").append(result.target()).append(System.lineSeparator());
        builder.append("Total files: ").append(result.totalFiles()).append(System.lineSeparator());
        builder.append("Total issues: ").append(result.totalIssues()).append(System.lineSeparator());
        builder.append("Severity summary: ");
        appendSeveritySummary(builder, result.severityCounts());
        builder.append(System.lineSeparator());
        builder.append("Spring structure summary: ");
        appendStructureSummary(builder, result);
        builder.append(System.lineSeparator());
        builder.append("Worst files:").append(System.lineSeparator());
        for (ReviewResult fileResult : result.worstFiles(5)) {
            builder.append("- ").append(fileResult.target())
                    .append(" (issues: ").append(fileResult.issues().size()).append(")")
                    .append(System.lineSeparator());
        }
        builder.append(System.lineSeparator());
        builder.append("Recommended actions:").append(System.lineSeparator());
        for (RecommendedAction action : result.recommendedActions(5)) {
            builder.append("- [").append(action.priority()).append("] ")
                    .append(action.title())
                    .append(" (issues: ").append(action.issueCount()).append(") — ")
                    .append(action.recommendation())
                    .append(System.lineSeparator());
        }
        builder.append(System.lineSeparator());

        for (ReviewResult fileResult : result.fileResults()) {
            appendSingleFile(builder, fileResult);
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }

    private void appendSingleFile(StringBuilder builder, ReviewResult result) {
        builder.append("Target: ").append(result.target()).append(System.lineSeparator());
        builder.append("Total issues: ").append(result.issues().size()).append(System.lineSeparator());
        builder.append("Severity summary: ");
        appendSeveritySummary(builder, result.severityCounts());
        builder.append(System.lineSeparator()).append(System.lineSeparator());

        if (result.issues().isEmpty()) {
            builder.append("No issues found.").append(System.lineSeparator());
            return;
        }

        for (ReviewIssue issue : result.issues()) {
            builder.append("[")
                    .append(issue.severity())
                    .append("] ")
                    .append(issue.ruleName())
                    .append(System.lineSeparator())
                    .append("  line ")
                    .append(issue.lineNumber())
                    .append(": ")
                    .append(issue.message())
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());
        }
    }

    static void appendSeveritySummary(StringBuilder builder, java.util.Map<Severity, Long> counts) {
        builder.append("HIGH=").append(counts.getOrDefault(Severity.HIGH, 0L))
                .append(", MEDIUM=").append(counts.getOrDefault(Severity.MEDIUM, 0L))
                .append(", LOW=").append(counts.getOrDefault(Severity.LOW, 0L));
    }

    static void appendStructureSummary(StringBuilder builder, ProjectReviewResult result) {
        builder.append("controller=").append(result.structureSummary().count("controller"))
                .append(", service=").append(result.structureSummary().count("service"))
                .append(", repository=").append(result.structureSummary().count("repository"))
                .append(", entity=").append(result.structureSummary().count("entity"))
                .append(", config=").append(result.structureSummary().count("config"))
                .append(", util=").append(result.structureSummary().count("util"))
                .append(", other=").append(result.structureSummary().count("other"));
    }
}
