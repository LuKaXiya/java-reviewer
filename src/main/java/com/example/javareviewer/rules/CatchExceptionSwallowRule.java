package com.example.javareviewer.rules;

import com.example.javareviewer.model.ReviewIssue;
import com.example.javareviewer.model.Severity;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class CatchExceptionSwallowRule extends AbstractLineRule {

    @Override
    public String name() {
        return "CatchExceptionSwallowRule";
    }

    @Override
    public List<ReviewIssue> evaluate(Path file, List<String> lines) {
        List<ReviewIssue> issues = issues();
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            if (!(line.contains("catch (Exception") || line.contains("catch(Exception")
                    || line.contains("catch (Throwable") || line.contains("catch(Throwable"))) {
                continue;
            }

            BlockAnalysis analysis = inspectCatchBlock(lines, index);
            if (analysis.swallowed) {
                issues.add(issue(index + 1,
                        "catch(Exception/Throwable) appears to swallow the error or return a default value; log and rethrow or handle explicitly.",
                        Severity.HIGH));
            } else {
                issues.add(issue(index + 1,
                        "Avoid broad catch(Exception/Throwable); catch narrower exception types when possible.",
                        Severity.MEDIUM));
            }
        }
        return issues;
    }

    private BlockAnalysis inspectCatchBlock(List<String> lines, int catchLineIndex) {
        boolean inside = false;
        int braceDepth = 0;
        boolean hasMeaningfulHandling = false;
        boolean hasReturnDefault = false;
        boolean sawCode = false;

        for (int index = catchLineIndex; index < lines.size(); index++) {
            String line = lines.get(index);
            if (!inside && line.contains("{")) {
                inside = true;
            }
            if (inside) {
                braceDepth += count(line, '{');
                String normalized = line.trim().toLowerCase(Locale.ROOT);
                if (!normalized.isBlank() && !normalized.equals("{") && !normalized.startsWith("catch ")) {
                    sawCode = true;
                }
                if (normalized.contains("throw ") || normalized.contains("logger.") || normalized.contains("log.")
                        || normalized.contains("log(") || normalized.contains("logger(") || normalized.contains("system.err")) {
                    hasMeaningfulHandling = true;
                }
                if (normalized.startsWith("return ") || normalized.contains(" return ")) {
                    if (normalized.contains("return null") || normalized.contains("return false") || normalized.contains("return true")
                            || normalized.contains("return 0") || normalized.contains("return -1")
                            || normalized.contains("return \"\"") || normalized.contains("return optional.empty()")
                            || normalized.contains("return collections.empty") || normalized.contains("return list.of()")
                            || normalized.contains("return map.of()") || normalized.contains("return new ")) {
                        hasReturnDefault = true;
                    }
                }
                braceDepth -= count(line, '}');
                if (braceDepth <= 0) {
                    break;
                }
            }
        }

        boolean swallowed = !hasMeaningfulHandling && (!sawCode || hasReturnDefault);
        return new BlockAnalysis(swallowed);
    }

    private int count(String text, char target) {
        int hits = 0;
        for (int index = 0; index < text.length(); index++) {
            if (text.charAt(index) == target) {
                hits++;
            }
        }
        return hits;
    }

    private record BlockAnalysis(boolean swallowed) {
    }
}
