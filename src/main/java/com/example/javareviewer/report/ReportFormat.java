package com.example.javareviewer.report;

public enum ReportFormat {
    TEXT,
    JSON,
    MARKDOWN;

    public static ReportFormat fromCliValue(String value) {
        return switch (value.toLowerCase()) {
            case "text" -> TEXT;
            case "json" -> JSON;
            case "markdown", "md" -> MARKDOWN;
            default -> throw new IllegalArgumentException("Unsupported format: " + value + ". Supported values: text, json, markdown");
        };
    }
}
