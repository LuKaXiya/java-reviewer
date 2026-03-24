package com.example.javareviewer.report;

import com.example.javareviewer.model.ProjectReviewResult;
import com.example.javareviewer.model.ReviewResult;

public interface ReportRenderer {

    String render(ReviewResult result);

    String render(ProjectReviewResult result);
}
