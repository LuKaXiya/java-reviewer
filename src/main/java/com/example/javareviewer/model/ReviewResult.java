package com.example.javareviewer.model;

import java.nio.file.Path;
import java.util.List;

public record ReviewResult(Path target, List<ReviewIssue> issues) {
}
