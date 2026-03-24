package com.example.javareviewer.report;

import com.example.javareviewer.model.ReviewIssue;
import com.example.javareviewer.model.ReviewResult;

public class ConsoleReportRenderer {

    public String render(ReviewResult result) {
        StringBuilder builder = new StringBuilder();
        builder.append("Java Reviewer Report").append(System.lineSeparator());
        builder.append("Target: ").append(result.target()).append(System.lineSeparator());
        builder.append("Total issues: ").append(result.issues().size()).append(System.lineSeparator());
        builder.append(System.lineSeparator());

        if (result.issues().isEmpty()) {
            builder.append("No issues found.").append(System.lineSeparator());
            return builder.toString();
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
        return builder.toString();
    }
}
