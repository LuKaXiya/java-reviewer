# java-reviewer

一个面向 **Java / Spring 项目** 的代码审查 CLI 原型。当前版本已经支持：

- 扫描单个 `.java` 文件
- 扫描整个目录（递归收集 `.java` 文件，自动忽略 `.git` / `target`）
- 输出单文件报告与项目级汇总报告
- 识别一批典型 Java / Spring 问题

## 已实现能力

### CLI / 扫描能力
- Maven + Java 21 标准项目结构
- CLI 入口：支持 **单文件** 与 **目录** 扫描
- 包结构：`cli`, `scanner`, `rules`, `report`, `model`
- 控制台文本报告输出

### 当前规则
- Controller 直接依赖 Repository
- Service 泄漏 Web 对象（`HttpServletRequest/Response`, `Model`, `ModelMap`, `WebRequest`）
- `printStackTrace()` 调用检查
- `@Transactional` 放在 Controller 或 private/final/static 方法上的风险提示
- `catch(Exception/Throwable)` 吞异常 / 返回默认值检查
- Controller 方法过多、职责可能过重提醒

### 项目级能力
- 统计总文件数
- 统计总问题数
- 按严重级别汇总（HIGH / MEDIUM / LOW）
- 输出最差文件列表

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

### 3. 扫描整个目录

```bash
mvn exec:java -Dexec.args="src/test/resources/samples"
```

或者先打包后直接运行：

```bash
mvn package
java -cp target/classes com.example.javareviewer.cli.JavaReviewerApplication src/test/resources/samples
```

## 输出示例

### 单文件报告

```text
Java Reviewer Report
Target: src/test/resources/samples/SampleController.java
Total issues: 2
Severity summary: HIGH=1, MEDIUM=1, LOW=0
```

### 项目汇总报告

```text
Java Reviewer Project Report
Target directory: src/test/resources/samples
Total files: 3
Total issues: 6
Severity summary: HIGH=3, MEDIUM=3, LOW=0
Worst files:
- src/test/resources/samples/SampleTransactionalController.java (issues: 3)
- src/test/resources/samples/SampleController.java (issues: 2)
```

## 后续可扩展方向

- 更稳健的 Java AST 解析
- JSON / Markdown 报告
- 规则分组、禁用、阈值控制
- Maven / Spring 项目结构自动识别
- AI 增强整改建议
