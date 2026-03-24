package com.example.javareviewer.cli;

import com.example.javareviewer.model.ProjectReviewResult;
import com.example.javareviewer.model.ReviewResult;
import com.example.javareviewer.report.ReportFormat;
import com.example.javareviewer.report.ReportRenderer;
import com.example.javareviewer.report.ReportRenderers;
import com.example.javareviewer.rules.BroadCatchExceptionRule;
import com.example.javareviewer.rules.CatchExceptionSwallowRule;
import com.example.javareviewer.rules.ConsolePrintRule;
import com.example.javareviewer.rules.ControllerBusinessLogicRule;
import com.example.javareviewer.rules.ControllerDirectRepositoryDependencyRule;
import com.example.javareviewer.rules.ControllerTooManyMethodsRule;
import com.example.javareviewer.rules.LoggingWithoutExceptionObjectRule;
import com.example.javareviewer.rules.PrintStackTraceRule;
import com.example.javareviewer.rules.RepositoryNamingAndResponsibilityRule;
import com.example.javareviewer.rules.ReviewRule;
import com.example.javareviewer.rules.ServiceWebObjectLeakRule;
import com.example.javareviewer.rules.ServiceWriteMethodWithoutTransactionalRule;
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
        CliOptions options;
        try {
            options = CliOptions.parse(args);
        } catch (IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
            System.err.println(CliOptions.usage());
            return 1;
        }

        Path target = options.target();
        if (!Files.exists(target)) {
            System.err.println("Target does not exist: " + target);
            return 1;
        }

        JavaFileScanner scanner = new JavaFileScanner(defaultRules());
        ReportRenderer renderer = ReportRenderers.forFormat(options.format());

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
                new ControllerBusinessLogicRule(),
                new ServiceWebObjectLeakRule(),
                new ServiceWriteMethodWithoutTransactionalRule(),
                new RepositoryNamingAndResponsibilityRule(),
                new LoggingWithoutExceptionObjectRule(),
                new ConsolePrintRule(),
                new PrintStackTraceRule(),
                new TransactionalRiskRule(),
                new BroadCatchExceptionRule(),
                new CatchExceptionSwallowRule(),
                new ControllerTooManyMethodsRule()
        );
    }

    record CliOptions(Path target, ReportFormat format) {
        static CliOptions parse(String[] args) {
            if (args.length == 0) {
                throw new IllegalArgumentException("Missing target path.");
            }

            Path target = null;
            ReportFormat format = ReportFormat.TEXT;

            for (int index = 0; index < args.length; index++) {
                String arg = args[index];
                if ("--format".equals(arg)) {
                    if (index + 1 >= args.length) {
                        throw new IllegalArgumentException("Missing value after --format.");
                    }
                    format = ReportFormat.fromCliValue(args[++index]);
                    continue;
                }

                if (arg.startsWith("--format=")) {
                    format = ReportFormat.fromCliValue(arg.substring("--format=".length()));
                    continue;
                }

                if (arg.startsWith("--")) {
                    throw new IllegalArgumentException("Unknown option: " + arg);
                }

                if (target != null) {
                    throw new IllegalArgumentException("Only one target path is supported.");
                }
                target = Path.of(arg);
            }

            if (target == null) {
                throw new IllegalArgumentException("Missing target path.");
            }
            return new CliOptions(target, format);
        }

        static String usage() {
            return "Usage: java-reviewer [--format text|json|markdown] <path-to-java-file-or-directory>";
        }
    }
}
