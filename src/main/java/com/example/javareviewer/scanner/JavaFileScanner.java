package com.example.javareviewer.scanner;

import com.example.javareviewer.model.ReviewIssue;
import com.example.javareviewer.model.ReviewResult;
import com.example.javareviewer.rules.ReviewRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
}
