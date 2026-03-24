package com.example.javareviewer.rules;

import com.example.javareviewer.model.ReviewIssue;
import com.example.javareviewer.model.Severity;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class ControllerBusinessLogicRule extends AbstractLineRule {

    @Override
    public String name() {
        return "ControllerBusinessLogicRule";
    }

    @Override
    public List<ReviewIssue> evaluate(Path file, List<String> lines) {
        List<ReviewIssue> issues = issues();
        boolean controller = lines.stream().anyMatch(line -> line.contains("@Controller") || line.contains("@RestController"));
        if (!controller) {
            return issues;
        }

        for (int index = 0; index < lines.size(); index++) {
            String normalized = lines.get(index).trim().toLowerCase(Locale.ROOT);
            if (normalized.startsWith("for ") || normalized.startsWith("for(")
                    || normalized.startsWith("while ") || normalized.startsWith("while(")
                    || normalized.startsWith("stream().") || normalized.contains("stream().")
                    || normalized.contains("repository.")
                    || normalized.contains("new bigdecimal(")
                    || normalized.contains("calculate")
                    || normalized.contains("validate")
                    || normalized.contains("convert")) {
                issues.add(issue(index + 1,
                        "Controller appears to contain business/data-processing logic; keep orchestration thin and move rules/calculation/looping into Service layer.",
                        Severity.MEDIUM));
            }
        }
        return issues;
    }
}
