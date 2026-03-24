package com.example.javareviewer.rules;

import com.example.javareviewer.model.ReviewIssue;
import com.example.javareviewer.model.Severity;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class ServiceWriteMethodWithoutTransactionalRule extends AbstractLineRule {

    private static final List<String> WRITE_METHOD_HINTS = List.of("save", "create", "update", "delete", "remove", "submit", "approve");

    @Override
    public String name() {
        return "ServiceWriteMethodWithoutTransactionalRule";
    }

    @Override
    public List<ReviewIssue> evaluate(Path file, List<String> lines) {
        List<ReviewIssue> issues = issues();
        boolean service = lines.stream().anyMatch(line -> line.contains("@Service") || line.contains("class ") && line.contains("Service"));
        if (!service) {
            return issues;
        }

        boolean classTransactional = lines.stream().anyMatch(line -> line.trim().startsWith("@Transactional"));
        if (classTransactional) {
            return issues;
        }

        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index).trim();
            if (!looksLikeMethod(line)) {
                continue;
            }
            String normalized = line.toLowerCase(Locale.ROOT);
            boolean writeMethod = WRITE_METHOD_HINTS.stream().anyMatch(normalized::contains);
            if (writeMethod) {
                issues.add(issue(index + 1,
                        "Service write-like method has no visible @Transactional boundary; verify transaction scope for data consistency.",
                        Severity.MEDIUM));
            }
        }
        return issues;
    }

    private boolean looksLikeMethod(String line) {
        return line.matches("(public|protected)\\s+.*\\(.*\\).*\\{?")
                && !line.startsWith("if ")
                && !line.startsWith("for ")
                && !line.startsWith("while ")
                && !line.startsWith("switch ");
    }
}
