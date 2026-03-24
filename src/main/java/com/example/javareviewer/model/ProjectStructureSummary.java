package com.example.javareviewer.model;

import java.util.LinkedHashMap;
import java.util.Map;

public record ProjectStructureSummary(Map<String, Long> roleCounts) {

    public ProjectStructureSummary {
        roleCounts = Map.copyOf(roleCounts);
    }

    public long count(String role) {
        return roleCounts.getOrDefault(role, 0L);
    }

    public static ProjectStructureSummary empty() {
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("controller", 0L);
        counts.put("service", 0L);
        counts.put("repository", 0L);
        counts.put("entity", 0L);
        counts.put("config", 0L);
        counts.put("util", 0L);
        counts.put("other", 0L);
        return new ProjectStructureSummary(counts);
    }
}
