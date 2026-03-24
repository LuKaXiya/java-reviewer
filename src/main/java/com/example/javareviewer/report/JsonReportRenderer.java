package com.example.javareviewer.report;

import com.example.javareviewer.model.ProjectReviewResult;
import com.example.javareviewer.model.ReviewIssue;
import com.example.javareviewer.model.ReviewResult;
import com.example.javareviewer.model.Severity;

import java.util.Map;
import java.util.StringJoiner;

public class JsonReportRenderer implements ReportRenderer {

    @Override
    public String render(ReviewResult result) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n")
                .append("  \"reportType\": \"single-file\",\n")
                .append("  \"target\": \"").append(escape(result.target().toString())).append("\",\n")
                .append("  \"totalIssues\": ").append(result.issues().size()).append(",\n")
                .append("  \"severitySummary\": ").append(severityJson(result.severityCounts())).append(",\n")
                .append("  \"issues\": [");

        appendIssues(builder, result);
        builder.append("\n  ]\n}");
        return builder.toString();
    }

    @Override
    public String render(ProjectReviewResult result) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n")
                .append("  \"reportType\": \"project\",\n")
                .append("  \"targetDirectory\": \"").append(escape(result.target().toString())).append("\",\n")
                .append("  \"totalFiles\": ").append(result.totalFiles()).append(",\n")
                .append("  \"totalIssues\": ").append(result.totalIssues()).append(",\n")
                .append("  \"severitySummary\": ").append(severityJson(result.severityCounts())).append(",\n")
                .append("  \"worstFiles\": [");

        for (int index = 0; index < result.worstFiles(5).size(); index++) {
            ReviewResult file = result.worstFiles(5).get(index);
            if (index > 0) {
                builder.append(",");
            }
            builder.append("\n    {\"target\": \"")
                    .append(escape(file.target().toString()))
                    .append("\", \"issueCount\": ")
                    .append(file.issues().size())
                    .append("}");
        }
        builder.append("\n  ],\n")
                .append("  \"files\": [");

        for (int index = 0; index < result.fileResults().size(); index++) {
            ReviewResult fileResult = result.fileResults().get(index);
            if (index > 0) {
                builder.append(",");
            }
            builder.append("\n    ").append(indent(renderFileObject(fileResult), 4));
        }
        builder.append("\n  ]\n}");
        return builder.toString();
    }

    private String renderFileObject(ReviewResult result) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n")
                .append("  \"target\": \"").append(escape(result.target().toString())).append("\",\n")
                .append("  \"totalIssues\": ").append(result.issues().size()).append(",\n")
                .append("  \"severitySummary\": ").append(severityJson(result.severityCounts())).append(",\n")
                .append("  \"issues\": [");
        appendIssues(builder, result);
        builder.append("\n  ]\n}");
        return builder.toString();
    }

    private void appendIssues(StringBuilder builder, ReviewResult result) {
        for (int index = 0; index < result.issues().size(); index++) {
            ReviewIssue issue = result.issues().get(index);
            if (index > 0) {
                builder.append(",");
            }
            builder.append("\n    {")
                    .append("\"ruleName\": \"").append(escape(issue.ruleName())).append("\", ")
                    .append("\"severity\": \"").append(issue.severity()).append("\", ")
                    .append("\"lineNumber\": ").append(issue.lineNumber()).append(", ")
                    .append("\"message\": \"").append(escape(issue.message())).append("\"")
                    .append("}");
        }
    }

    private String severityJson(Map<Severity, Long> counts) {
        return new StringJoiner(", ", "{", "}")
                .add("\"HIGH\": " + counts.getOrDefault(Severity.HIGH, 0L))
                .add("\"MEDIUM\": " + counts.getOrDefault(Severity.MEDIUM, 0L))
                .add("\"LOW\": " + counts.getOrDefault(Severity.LOW, 0L))
                .toString();
    }

    private String indent(String value, int spaces) {
        String prefix = " ".repeat(spaces);
        return prefix + value.replace("\n", "\n" + prefix).trim();
    }

    private String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
