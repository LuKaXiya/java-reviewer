package com.example.javareviewer.report;

public final class ReportRenderers {

    private ReportRenderers() {
    }

    public static ReportRenderer forFormat(ReportFormat format) {
        return switch (format) {
            case TEXT -> new ConsoleReportRenderer();
            case JSON -> new JsonReportRenderer();
            case MARKDOWN -> new MarkdownReportRenderer();
        };
    }
}
