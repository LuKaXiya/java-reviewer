package com.example.javareviewer.scanner;

import com.example.javareviewer.model.ProjectStructureSummary;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SpringProjectStructureAnalyzer {

    private static final List<String> ROLES = List.of("controller", "service", "repository", "entity", "config", "util", "other");

    public ProjectStructureSummary summarize(Map<Path, List<String>> fileContents) {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (String role : ROLES) {
            counts.put(role, 0L);
        }
        for (Map.Entry<Path, List<String>> entry : fileContents.entrySet()) {
            String role = detectRole(entry.getKey(), entry.getValue());
            counts.merge(role, 1L, Long::sum);
        }
        return new ProjectStructureSummary(counts);
    }

    String detectRole(Path file, List<String> lines) {
        String pathValue = file.toString().toLowerCase(Locale.ROOT).replace('\\', '/');
        String content = String.join("\n", lines).toLowerCase(Locale.ROOT);
        String fileName = file.getFileName().toString().toLowerCase(Locale.ROOT);

        if (pathValue.contains("/controller/") || content.contains("@restcontroller") || content.contains("@controller") || fileName.endsWith("controller.java")) {
            return "controller";
        }
        if (pathValue.contains("/service/") || content.contains("@service") || fileName.endsWith("service.java") || fileName.endsWith("serviceimpl.java")) {
            return "service";
        }
        if (pathValue.contains("/repository/") || pathValue.contains("/dao/") || content.contains("@repository") || fileName.endsWith("repository.java") || fileName.endsWith("dao.java")) {
            return "repository";
        }
        if (pathValue.contains("/entity/") || pathValue.contains("/domain/") || content.contains("@entity") || content.contains("@table(") || fileName.endsWith("entity.java")) {
            return "entity";
        }
        if (pathValue.contains("/config/") || content.contains("@configuration") || fileName.endsWith("config.java") || fileName.endsWith("configuration.java")) {
            return "config";
        }
        if (pathValue.contains("/util/") || pathValue.contains("/utils/") || fileName.endsWith("util.java") || fileName.endsWith("utils.java") || fileName.endsWith("helper.java")) {
            return "util";
        }
        return "other";
    }
}
