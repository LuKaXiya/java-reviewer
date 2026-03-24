package com.example.javareviewer.rules;

import com.example.javareviewer.model.ReviewIssue;
import com.example.javareviewer.model.Severity;

import java.nio.file.Path;
import java.util.List;

public class ControllerTooManyMethodsRule extends AbstractLineRule {

    private static final int METHOD_THRESHOLD = 8;

    @Override
    public String name() {
        return "ControllerTooManyMethodsRule";
    }

    @Override
    public List<ReviewIssue> evaluate(Path file, List<String> lines) {
        List<ReviewIssue> issues = issues();
        boolean controller = lines.stream().anyMatch(line -> line.contains("@Controller") || line.contains("@RestController"));
        if (!controller) {
            return issues;
        }

        int methodCount = 0;
        for (String line : lines) {
            String trimmed = line.trim();
            boolean looksLikeMethod = trimmed.matches("(public|protected|private)\\s+.*\\(.*\\).*\\{?")
                    && !trimmed.startsWith("if ")
                    && !trimmed.startsWith("for ")
                    && !trimmed.startsWith("while ")
                    && !trimmed.startsWith("switch ");
            if (looksLikeMethod) {
                methodCount++;
            }
        }

        if (methodCount > METHOD_THRESHOLD) {
            issues.add(issue(1,
                    "Controller contains too many methods and likely mixes multiple responsibilities.",
                    Severity.MEDIUM));
        }
        return issues;
    }
}
