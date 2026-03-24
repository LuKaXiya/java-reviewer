package com.example.javareviewer.rules;

import com.example.javareviewer.model.ReviewIssue;
import com.example.javareviewer.model.Severity;

import java.nio.file.Path;
import java.util.List;

public class ServiceWebObjectLeakRule extends AbstractLineRule {

    private static final List<String> WEB_TYPES = List.of(
            "HttpServletRequest",
            "HttpServletResponse",
            "Model",
            "ModelMap",
            "WebRequest"
    );

    @Override
    public String name() {
        return "ServiceWebObjectLeakRule";
    }

    @Override
    public List<ReviewIssue> evaluate(Path file, List<String> lines) {
        List<ReviewIssue> issues = issues();
        boolean service = lines.stream().anyMatch(line -> line.contains("@Service") || line.contains("class ") && line.contains("Service"));
        if (!service) {
            return issues;
        }

        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            boolean hitsWebType = WEB_TYPES.stream().anyMatch(line::contains);
            if (hitsWebType) {
                issues.add(issue(index + 1,
                        "Service layer should not expose or depend on web-specific objects.",
                        Severity.MEDIUM));
            }
        }
        return issues;
    }
}
