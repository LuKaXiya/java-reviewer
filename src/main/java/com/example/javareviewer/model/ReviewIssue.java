package com.example.javareviewer.model;

public record ReviewIssue(
        String ruleName,
        Severity severity,
        int lineNumber,
        String message
) {
}
