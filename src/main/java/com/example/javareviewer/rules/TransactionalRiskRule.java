package com.example.javareviewer.rules;

import com.example.javareviewer.model.ReviewIssue;
import com.example.javareviewer.model.Severity;

import java.nio.file.Path;
import java.util.List;

public class TransactionalRiskRule extends AbstractLineRule {

    @Override
    public String name() {
        return "TransactionalRiskRule";
    }

    @Override
    public List<ReviewIssue> evaluate(Path file, List<String> lines) {
        List<ReviewIssue> issues = issues();
        boolean controller = lines.stream().anyMatch(line -> line.contains("@Controller") || line.contains("@RestController"));

        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index).trim();
            if (!line.startsWith("@Transactional")) {
                continue;
            }

            if (controller) {
                issues.add(issue(index + 1,
                        "Avoid placing @Transactional in Controller; keep transaction boundaries in the service layer.",
                        Severity.HIGH));
            }

            String methodLine = findNextCodeLine(lines, index + 1);
            if (methodLine != null && methodLine.contains("(") && methodLine.contains(")")
                    && (methodLine.contains(" private ") || methodLine.startsWith("private ")
                    || methodLine.contains(" final ") || methodLine.startsWith("final ")
                    || methodLine.contains(" static ") || methodLine.startsWith("static "))) {
                issues.add(issue(index + 1,
                        "@Transactional on private/final/static methods is risky because proxy-based AOP may not apply as expected.",
                        Severity.MEDIUM));
            }
        }
        return issues;
    }

    private String findNextCodeLine(List<String> lines, int startIndex) {
        for (int index = startIndex; index < lines.size(); index++) {
            String line = lines.get(index).trim();
            if (!line.isEmpty() && !line.startsWith("@")) {
                return " " + line + " ";
            }
        }
        return null;
    }
}
