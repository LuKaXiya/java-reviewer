package com.example.javareviewer.rules;

import com.example.javareviewer.model.ReviewIssue;
import com.example.javareviewer.model.Severity;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class RepositoryNamingAndResponsibilityRule extends AbstractLineRule {

    @Override
    public String name() {
        return "RepositoryNamingAndResponsibilityRule";
    }

    @Override
    public List<ReviewIssue> evaluate(Path file, List<String> lines) {
        List<ReviewIssue> issues = issues();
        String joined = String.join("\n", lines);
        boolean repository = joined.contains("@Repository") || file.getFileName().toString().endsWith("Repository.java")
                || file.getFileName().toString().endsWith("Dao.java");
        if (!repository) {
            return issues;
        }

        String fileName = file.getFileName().toString();
        boolean mixedNaming = fileName.endsWith("Dao.java") && joined.contains("@Repository");
        if (mixedNaming) {
            issues.add(issue(1,
                    "Repository/DAO naming is mixed (DAO suffix with @Repository). Standardize repository abstraction naming for consistency.",
                    Severity.LOW));
        }

        for (int index = 0; index < lines.size(); index++) {
            String normalized = lines.get(index).trim().toLowerCase(Locale.ROOT);
            if (normalized.contains("httpservletrequest") || normalized.contains("responseentity")
                    || normalized.contains("resttemplate") || normalized.contains("webclient")
                    || normalized.contains("business") || normalized.contains("calculate")
                    || normalized.contains("validate")) {
                issues.add(issue(index + 1,
                        "Repository/DAO appears to contain web or business logic concerns; keep persistence layer focused on data access.",
                        Severity.MEDIUM));
            }
        }
        return issues;
    }
}
