# java-reviewer

一个面向 Java / Spring 项目的代码审查 CLI 原型，当前聚焦对单个 `.java` 文件执行本地静态规则扫描并输出控制台报告。

## 已实现能力

- Maven + Java 21 标准项目结构
- CLI 入口：接收单个 `.java` 文件路径
- 清晰包结构：`cli`, `scanner`, `rules`, `report`, `model`
- 首批规则：
  - Controller 直接依赖 Repository
  - Service 泄漏 Web 对象（`HttpServletRequest/Response`, `Model`, `ModelMap`, `WebRequest`）
  - `printStackTrace` 调用检查
- 控制台文本报告输出
- JUnit 5 最小测试覆盖

## 运行要求

- JDK 21
- Maven 3.9+

## 运行方式

### 1. 编译与测试

```bash
mvn test
```

### 2. 扫描单个 Java 文件

```bash
mvn exec:java -Dexec.args="src/test/resources/samples/SampleController.java"
```

或者先打包后直接运行：

```bash
mvn package
java -cp target/classes com.example.javareviewer.cli.JavaReviewerApplication src/test/resources/samples/SampleController.java
```

## 输出示例

```text
Java Reviewer Report
Target: src/test/resources/samples/SampleController.java
Total issues: 2

[HIGH] ControllerDirectRepositoryDependencyRule
  line 8: Controller should depend on Service instead of Repository directly.

[MEDIUM] PrintStackTraceRule
  line 17: Avoid calling printStackTrace(); use structured logging instead.
```

## 后续可扩展方向

- 支持目录递归扫描
- 更稳健的 Java AST 解析
- 支持 JSON/Markdown 报告
- 规则分组、禁用、阈值控制
