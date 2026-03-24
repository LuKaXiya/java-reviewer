package com.example.javareviewer;

import com.example.javareviewer.rules.BroadCatchExceptionRule;
import com.example.javareviewer.rules.CatchExceptionSwallowRule;
import com.example.javareviewer.rules.ConsolePrintRule;
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
    void shouldFindControllerRepositoryConsolePrintAndPrintStackTraceIssues() throws Exception {
        JavaFileScanner scanner = new JavaFileScanner(List.of(
                new ControllerDirectRepositoryDependencyRule(),
                new ConsolePrintRule(),
                new PrintStackTraceRule()
        ));

        var result = scanner.scan(Path.of("src/test/resources/samples/SampleController.java"));

        assertEquals(3, result.issues().size());
        assertTrue(result.issues().stream().anyMatch(issue -> issue.ruleName().equals("ControllerDirectRepositoryDependencyRule")));
        assertTrue(result.issues().stream().anyMatch(issue -> issue.ruleName().equals("ConsolePrintRule")));
        assertTrue(result.issues().stream().anyMatch(issue -> issue.ruleName().equals("PrintStackTraceRule")));
    }

    @Test
    void shouldFindServiceWebLeakIssues() throws Exception {
        JavaFileScanner scanner = new JavaFileScanner(List.of(new ServiceWebObjectLeakRule()));

        var result = scanner.scan(Path.of("src/test/resources/samples/SampleService.java"));

        assertEquals(3, result.issues().size());
    }

    @Test
    void shouldFindTransactionalBroadCatchAndSwallowIssues() throws Exception {
        JavaFileScanner scanner = new JavaFileScanner(List.of(
                new TransactionalRiskRule(),
                new BroadCatchExceptionRule(),
                new CatchExceptionSwallowRule()
        ));

        var result = scanner.scan(Path.of("src/test/resources/samples/SampleTransactionalController.java"));

        assertTrue(result.issues().stream().anyMatch(issue -> issue.ruleName().equals("TransactionalRiskRule")));
        assertTrue(result.issues().stream().anyMatch(issue -> issue.ruleName().equals("BroadCatchExceptionRule")));
        assertTrue(result.issues().stream().anyMatch(issue -> issue.ruleName().equals("CatchExceptionSwallowRule")));
    }

    @Test
    void shouldScanDirectoryAndAggregateResults() throws Exception {
        JavaFileScanner scanner = new JavaFileScanner(List.of(
                new ControllerDirectRepositoryDependencyRule(),
                new ServiceWebObjectLeakRule(),
                new ConsolePrintRule(),
                new PrintStackTraceRule(),
                new TransactionalRiskRule(),
                new BroadCatchExceptionRule(),
                new CatchExceptionSwallowRule(),
                new ControllerTooManyMethodsRule()
        ));

        var result = scanner.scanDirectory(Path.of("src/test/resources/samples"));

        assertEquals(3, result.totalFiles());
        assertTrue(result.totalIssues() >= 7);
        assertTrue(result.worstFiles(2).size() <= 2);
    }
}
