package com.example.javareviewer.cli;

import com.example.javareviewer.model.ProjectReviewResult;
import com.example.javareviewer.model.ReviewResult;
import com.example.javareviewer.report.ConsoleReportRenderer;
import com.example.javareviewer.rules.CatchExceptionSwallowRule;
import com.example.javareviewer.rules.ControllerDirectRepositoryDependencyRule;
import com.example.javareviewer.rules.ControllerTooManyMethodsRule;
import com.example.javareviewer.rules.PrintStackTraceRule;
import com.example.javareviewer.rules.ReviewRule;
import com.example.javareviewer.rules.ServiceWebObjectLeakRule;
import com.example.javareviewer.rules.TransactionalRiskRule;
import com.example.javareviewer.scanner.JavaFileScanner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class JavaReviewerApplication {

    public static void main(String[] args) {
        int exitCode = new JavaReviewerApplication().run(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    int run(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java-reviewer <path-to-java-file-or-directory>");
            return 1;
        }

        Path target = Path.of(args[0]);
        if (!Files.exists(target)) {
            System.err.println("Target does not exist: " + target);
            return 1;
        }

        JavaFileScanner scanner = new JavaFileScanner(defaultRules());
        ConsoleReportRenderer renderer = new ConsoleReportRenderer();

        try {
            if (Files.isDirectory(target)) {
                ProjectReviewResult result = scanner.scanDirectory(target);
                System.out.print(renderer.render(result));
                return 0;
            }

            if (!target.toString().endsWith(".java")) {
                System.err.println("Target must be a .java file or a directory: " + target);
                return 1;
            }

            ReviewResult result = scanner.scan(target);
            System.out.print(renderer.render(result));
            return 0;
        } catch (IOException exception) {
            System.err.println("Failed to scan target: " + exception.getMessage());
            return 2;
        }
    }

    private List<ReviewRule> defaultRules() {
        return List.of(
                new ControllerDirectRepositoryDependencyRule(),
                new ServiceWebObjectLeakRule(),
                new PrintStackTraceRule(),
                new TransactionalRiskRule(),
                new CatchExceptionSwallowRule(),
                new ControllerTooManyMethodsRule()
        );
    }
}
