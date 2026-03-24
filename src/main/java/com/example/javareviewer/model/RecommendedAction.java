package com.example.javareviewer.model;

public record RecommendedAction(
        Severity priority,
        String title,
        long issueCount,
        String recommendation
) {
}
