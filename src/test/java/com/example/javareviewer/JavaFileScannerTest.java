package com.example.javareviewer;

import com.example.javareviewer.rules.CatchExceptionSwallowRule;
import com.example.javareviewer.rules.ControllerDirectRepositoryDependencyRule;
import com.example.javareviewer.rules.ControllerTooManyMethodsRule;
import com.example.javareviewer.rules.PrintStackTraceRule;
import com.example.javareviewer.rules.ServiceWebObjectLeakRule;
import com.example.javareviewer.rules.TransactionalRiskRule;
import com.example.javareviewer.scanner.JavaFileScanner;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaFileScannerTest {

    @Test
    void shouldFindControllerRepositoryAndPrintStackTraceIssues() throws Exception {
        JavaFileScanner scanner = new JavaFileScanner(List.of(
                new ControllerDirectRepositoryDependencyRule(),
                new PrintStackTraceRule()
        ));

        var result = scanner.scan(Path.of("src/test/resources/samples/SampleController.java"));

        assertEquals(2, result.issues().size());
        assertTrue(result.issues().stream().anyMatch(issue -> issue.ruleName().equals("ControllerDirectRepositoryDependencyRule")));
        assertTrue(result.issues().stream().anyMatch(issue -> issue.ruleName().equals("PrintStackTraceRule")));
    }

    @Test
    void shouldFindServiceWebLeakIssues() throws Exception {
        JavaFileScanner scanner = new JavaFileScanner(List.of(new ServiceWebObjectLeakRule()));

        var result = scanner.scan(Path.of("src/test/resources/samples/SampleService.java"));

        assertEquals(3, result.issues().size());
    }

    @Test
    void shouldFindTransactionalAndSwallowIssues() throws Exception {
        JavaFileScanner scanner = new JavaFileScanner(List.of(
                new TransactionalRiskRule(),
                new CatchExceptionSwallowRule()
        ));

        var result = scanner.scan(Path.of("src/test/resources/samples/SampleTransactionalController.java"));

        assertTrue(result.issues().stream().anyMatch(issue -> issue.ruleName().equals("TransactionalRiskRule")));
        assertTrue(result.issues().stream().anyMatch(issue -> issue.ruleName().equals("CatchExceptionSwallowRule")));
    }

    @Test
    void shouldScanDirectoryAndAggregateResults() throws Exception {
        JavaFileScanner scanner = new JavaFileScanner(List.of(
                new ControllerDirectRepositoryDependencyRule(),
                new ServiceWebObjectLeakRule(),
                new PrintStackTraceRule(),
                new TransactionalRiskRule(),
                new CatchExceptionSwallowRule(),
                new ControllerTooManyMethodsRule()
        ));

        var result = scanner.scanDirectory(Path.of("src/test/resources/samples"));

        assertEquals(3, result.totalFiles());
        assertTrue(result.totalIssues() >= 5);
        assertTrue(result.worstFiles(2).size() <= 2);
    }
}
