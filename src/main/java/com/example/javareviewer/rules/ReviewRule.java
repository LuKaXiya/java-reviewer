package com.example.javareviewer.rules;

import com.example.javareviewer.model.ReviewIssue;

import java.nio.file.Path;
import java.util.List;

public interface ReviewRule {

    String name();

    List<ReviewIssue> evaluate(Path file, List<String> lines);
}
