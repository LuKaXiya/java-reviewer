package com.example.javareviewer.rules;

import com.example.javareviewer.model.ReviewIssue;
import com.example.javareviewer.model.Severity;

import java.nio.file.Path;
import java.util.List;

public class ConsolePrintRule extends AbstractLineRule {

    @Override
    public String name() {
        return "ConsolePrintRule";
    }

    @Override
    public List<ReviewIssue> evaluate(Path file, List<String> lines) {
        List<ReviewIssue> issues = issues();
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            if (line.contains("System.out.print(") || line.contains("System.out.println(") || line.contains("System.out.printf(")
                    || line.contains("System.err.print(") || line.contains("System.err.println(") || line.contains("System.err.printf(")) {
                issues.add(issue(index + 1,
                        "Avoid System.out/System.err printing in application code; use a structured logger (for example SLF4J) instead.",
                        Severity.MEDIUM));
            }
        }
        return issues;
    }
}
