package com.example.javareviewer.rules;

import com.example.javareviewer.model.ReviewIssue;
import com.example.javareviewer.model.Severity;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractLineRule implements ReviewRule {

    protected ReviewIssue issue(int lineNumber, String message, Severity severity) {
        return new ReviewIssue(name(), severity, lineNumber, message);
    }

    protected List<ReviewIssue> issues() {
        return new ArrayList<>();
    }
}
