package com.example.javareviewer.model;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public record ReviewResult(Path target, List<ReviewIssue> issues) {

    public Map<Severity, Long> severityCounts() {
        Map<Severity, Long> counts = new EnumMap<>(Severity.class);
        for (Severity severity : Severity.values()) {
            counts.put(severity, 0L);
        }
        for (ReviewIssue issue : issues) {
            counts.merge(issue.severity(), 1L, Long::sum);
        }
        return counts;
    }
}
