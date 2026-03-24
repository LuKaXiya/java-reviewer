package com.example.javareviewer.rules;

import com.example.javareviewer.model.ReviewIssue;
import com.example.javareviewer.model.Severity;

import java.nio.file.Path;
import java.util.List;

public class BroadCatchExceptionRule extends AbstractLineRule {

    @Override
    public String name() {
        return "BroadCatchExceptionRule";
    }

    @Override
    public List<ReviewIssue> evaluate(Path file, List<String> lines) {
        List<ReviewIssue> issues = issues();
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            if (line.contains("catch (Exception") || line.contains("catch(Exception")
                    || line.contains("catch (Throwable") || line.contains("catch(Throwable")) {
                issues.add(issue(index + 1,
                        "Avoid catch(Exception/Throwable); prefer narrower exception types and let unexpected errors surface clearly.",
                        Severity.MEDIUM));
            }
        }
        return issues;
    }
}
