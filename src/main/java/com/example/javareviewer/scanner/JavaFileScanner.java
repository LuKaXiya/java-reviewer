package com.example.javareviewer.scanner;

import com.example.javareviewer.model.ProjectReviewResult;
import com.example.javareviewer.model.ReviewIssue;
import com.example.javareviewer.model.ReviewResult;
import com.example.javareviewer.rules.ReviewRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class JavaFileScanner {

    private final List<ReviewRule> rules;

    public JavaFileScanner(List<ReviewRule> rules) {
        this.rules = List.copyOf(rules);
    }

    public ReviewResult scan(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        List<ReviewIssue> issues = new ArrayList<>();
        for (ReviewRule rule : rules) {
            issues.addAll(rule.evaluate(file, lines));
        }
        issues.sort(Comparator.comparingInt(ReviewIssue::lineNumber).thenComparing(ReviewIssue::ruleName));
        return new ReviewResult(file, List.copyOf(issues));
    }

    public ProjectReviewResult scanDirectory(Path directory) throws IOException {
        try (Stream<Path> stream = Files.walk(directory)) {
            List<ReviewResult> results = stream
                    .filter(Files::isRegularFile)
                    .filter(this::isJavaFile)
                    .filter(path -> !isIgnored(path))
                    .sorted()
                    .map(path -> {
                        try {
                            return scan(path);
                        } catch (IOException exception) {
                            throw new DirectoryScanException(path, exception);
                        }
                    })
                    .toList();
            return new ProjectReviewResult(directory, results);
        } catch (DirectoryScanException exception) {
            throw exception.getCause();
        }
    }

    private boolean isJavaFile(Path path) {
        return path.toString().endsWith(".java");
    }

    private boolean isIgnored(Path path) {
        for (Path part : path) {
            String value = part.toString();
            if ("target".equals(value) || ".git".equals(value)) {
                return true;
            }
        }
        return false;
    }

    private static final class DirectoryScanException extends RuntimeException {
        private final IOException cause;

        private DirectoryScanException(Path path, IOException cause) {
            super("Failed to scan file: " + path, cause);
            this.cause = cause;
        }

        @Override
        public IOException getCause() {
            return cause;
        }
    }
}
