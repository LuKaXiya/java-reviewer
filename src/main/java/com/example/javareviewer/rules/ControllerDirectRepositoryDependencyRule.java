package com.example.javareviewer.rules;

import com.example.javareviewer.model.ReviewIssue;
import com.example.javareviewer.model.Severity;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class ControllerDirectRepositoryDependencyRule extends AbstractLineRule {

    @Override
    public String name() {
        return "ControllerDirectRepositoryDependencyRule";
    }

    @Override
    public List<ReviewIssue> evaluate(Path file, List<String> lines) {
        List<ReviewIssue> issues = issues();
        boolean controller = lines.stream().anyMatch(line -> line.contains("@Controller") || line.contains("@RestController"));
        if (!controller) {
            return issues;
        }

        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            String normalized = line.toLowerCase(Locale.ROOT);
            boolean referencesRepositoryType = normalized.contains("repository ")
                    || normalized.contains("repository;")
                    || normalized.contains("repository)")
                    || normalized.contains("repository,")
                    || normalized.contains("repository<");
            boolean looksLikeFieldOrInjection = line.contains("private") || line.contains("final")
                    || line.contains("@Autowired") || line.contains("public ");

            if (referencesRepositoryType && looksLikeFieldOrInjection) {
                issues.add(issue(index + 1,
                        "Controller should depend on Service instead of Repository directly.",
                        Severity.HIGH));
            }
        }
        return issues;
    }
}
