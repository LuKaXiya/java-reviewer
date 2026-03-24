package com.example.javareviewer.cli;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaReviewerApplicationTest {

    @Test
    void shouldRenderJsonForSingleFile() {
        JavaReviewerApplication application = new JavaReviewerApplication();
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(stdout, true, StandardCharsets.UTF_8));
        try {
            int exitCode = application.run(new String[]{"--format", "json", "src/test/resources/samples/SampleController.java"});

            assertEquals(0, exitCode);
            String output = stdout.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("\"reportType\": \"single-file\""));
            assertTrue(output.contains("\"ConsolePrintRule\""));
            assertTrue(output.contains("\"PrintStackTraceRule\""));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void shouldRenderMarkdownForDirectory() {
        JavaReviewerApplication application = new JavaReviewerApplication();
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(stdout, true, StandardCharsets.UTF_8));
        try {
            int exitCode = application.run(new String[]{"src/test/resources/samples", "--format=markdown"});

            assertEquals(0, exitCode);
            String output = stdout.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("# Java Reviewer Project Report"));
            assertTrue(output.contains("## Worst files"));
            assertTrue(output.contains("### `src/test/resources/samples/SampleController.java`"));
        } finally {
            System.setOut(originalOut);
        }
    }
}
