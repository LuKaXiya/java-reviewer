package com.example.javareviewer.rules;

import com.example.javareviewer.model.ReviewIssue;
import com.example.javareviewer.model.Severity;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoggingWithoutExceptionObjectRule extends AbstractLineRule {

    private static final Pattern CATCH_PATTERN = Pattern.compile("catch\\s*\\(\\s*[\\w<>]+\\s+(\\w+)\\s*\\)");

    @Override
    public String name() {
        return "LoggingWithoutExceptionObjectRule";
    }

    @Override
    public List<ReviewIssue> evaluate(Path file, List<String> lines) {
        List<ReviewIssue> issues = issues();
        for (int index = 0; index < lines.size(); index++) {
            Matcher matcher = CATCH_PATTERN.matcher(lines.get(index));
            if (!matcher.find()) {
                continue;
            }

            String exceptionVar = matcher.group(1);
            if (hasLoggerCallWithoutException(lines, index, exceptionVar)) {
                issues.add(issue(index + 1,
                        "Exception is logged without passing the caught exception object; include the throwable so stack trace and root cause are preserved.",
                        Severity.MEDIUM));
            }
        }
        return issues;
    }

    private boolean hasLoggerCallWithoutException(List<String> lines, int catchLineIndex, String exceptionVar) {
        boolean inside = false;
        int braceDepth = 0;
        for (int index = catchLineIndex; index < lines.size(); index++) {
            String line = lines.get(index);
            if (!inside && line.contains("{")) {
                inside = true;
            }
            if (!inside) {
                continue;
            }

            braceDepth += count(line, '{');
            String normalized = line.trim().toLowerCase(Locale.ROOT);
            boolean loggerCall = normalized.contains("log.error(") || normalized.contains("logger.error(")
                    || normalized.contains("log.warn(") || normalized.contains("logger.warn(");
            if (loggerCall && !normalized.contains(exceptionVar.toLowerCase(Locale.ROOT))) {
                return true;
            }
            braceDepth -= count(line, '}');
            if (braceDepth <= 0) {
                break;
            }
        }
        return false;
    }

    private int count(String text, char target) {
        int hits = 0;
        for (int index = 0; index < text.length(); index++) {
            if (text.charAt(index) == target) {
                hits++;
            }
        }
        return hits;
    }
}
