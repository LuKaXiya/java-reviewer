package com.example.javareviewer;

import com.example.javareviewer.rules.ControllerDirectRepositoryDependencyRule;
import com.example.javareviewer.rules.PrintStackTraceRule;
import com.example.javareviewer.rules.ServiceWebObjectLeakRule;
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
}
