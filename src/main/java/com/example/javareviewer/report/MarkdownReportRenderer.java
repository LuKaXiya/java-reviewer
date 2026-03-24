package com.example.javareviewer.report;

import com.example.javareviewer.model.ProjectReviewResult;
import com.example.javareviewer.model.ReviewIssue;
import com.example.javareviewer.model.ReviewResult;

public class MarkdownReportRenderer implements ReportRenderer {

    @Override
    public String render(ReviewResult result) {
        StringBuilder builder = new StringBuilder();
        builder.append("# Java Reviewer Report\n\n")
                .append("- **Target:** ").append(result.target()).append("\n")
                .append("- **Total issues:** ").append(result.issues().size()).append("\n")
                .append("- **Severity summary:** ");
        ConsoleReportRenderer.appendSeveritySummary(builder, result.severityCounts());
        builder.append("\n\n");

        if (result.issues().isEmpty()) {
            builder.append("No issues found.\n");
            return builder.toString();
        }

        builder.append("## Issues\n\n");
        for (ReviewIssue issue : result.issues()) {
            builder.append("- **[")
                    .append(issue.severity())
                    .append("] ")
                    .append(issue.ruleName())
                    .append("** — line ")
                    .append(issue.lineNumber())
                    .append(": ")
                    .append(issue.message())
                    .append("\n");
        }
        return builder.toString();
    }

    @Override
    public String render(ProjectReviewResult result) {
        StringBuilder builder = new StringBuilder();
        builder.append("# Java Reviewer Project Report\n\n")
                .append("- **Target directory:** ").append(result.target()).append("\n")
                .append("- **Total files:** ").append(result.totalFiles()).append("\n")
                .append("- **Total issues:** ").append(result.totalIssues()).append("\n")
                .append("- **Severity summary:** ");
        ConsoleReportRenderer.appendSeveritySummary(builder, result.severityCounts());
        builder.append("\n\n## Worst files\n\n");

        for (ReviewResult fileResult : result.worstFiles(5)) {
            builder.append("- `")
                    .append(fileResult.target())
                    .append("` — ")
                    .append(fileResult.issues().size())
                    .append(" issues\n");
        }

        builder.append("\n## File details\n\n");
        for (ReviewResult fileResult : result.fileResults()) {
            builder.append("### `").append(fileResult.target()).append("`\n\n")
                    .append("- Total issues: ").append(fileResult.issues().size()).append("\n")
                    .append("- Severity summary: ");
            ConsoleReportRenderer.appendSeveritySummary(builder, fileResult.severityCounts());
            builder.append("\n");
            if (fileResult.issues().isEmpty()) {
                builder.append("- No issues found.\n\n");
                continue;
            }
            builder.append("\n");
            for (ReviewIssue issue : fileResult.issues()) {
                builder.append("  - **[")
                        .append(issue.severity())
                        .append("] ")
                        .append(issue.ruleName())
                        .append("** — line ")
                        .append(issue.lineNumber())
                        .append(": ")
                        .append(issue.message())
                        .append("\n");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
