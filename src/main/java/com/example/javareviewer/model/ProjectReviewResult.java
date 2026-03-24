package com.example.javareviewer.model;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public record ProjectReviewResult(Path target, List<ReviewResult> fileResults) {

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
}
