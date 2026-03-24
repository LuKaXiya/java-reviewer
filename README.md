# java-reviewer

一个面向 **Java / Spring 项目** 的代码审查 CLI 原型。当前版本已经支持：

- 扫描单个 `.java` 文件
- 扫描整个目录（递归收集 `.java` 文件，自动忽略 `.git` / `target`）
- 输出文本、JSON、Markdown 三种报告格式
- 识别一批典型 Java / Spring 问题

## 已实现能力

### CLI / 扫描能力
- Maven + Java 21 标准项目结构
- CLI 入口：支持 **单文件** 与 **目录** 扫描
- CLI 参数：支持 `--format text|json|markdown`
- 包结构：`cli`, `scanner`, `rules`, `report`, `model`
- 文本、JSON、Markdown 报告输出

### 当前规则
- Controller 直接依赖 Repository
- Service 泄漏 Web 对象（`HttpServletRequest/Response`, `Model`, `ModelMap`, `WebRequest`）
- `System.out/System.err print/println/printf` 输出替代提醒（建议改为结构化日志）
- `printStackTrace()` 调用检查
- `@Transactional` 放在 Controller 或 private/final/static 方法上的风险提示
- `catch(Exception/Throwable)` 过宽捕获提醒
- `catch(Exception/Throwable)` 吞异常 / 返回默认值检查
- Controller 方法过多、职责可能过重提醒

### 项目级能力
- 统计总文件数
- 统计总问题数
- 按严重级别汇总（HIGH / MEDIUM / LOW）
- 输出最差文件列表
- JSON 目录级完整文件结果导出
- Markdown 项目汇总与文件详情输出

## 运行要求

- JDK 21
- Maven 3.9+

## 运行方式

### 1. 编译与测试

```bash
mvn test
```

### 2. 扫描单个 Java 文件

默认文本格式：

```bash
mvn exec:java -Dexec.args="src/test/resources/samples/SampleController.java"
```

输出 JSON：

```bash
mvn exec:java -Dexec.args="--format json src/test/resources/samples/SampleController.java"
```

输出 Markdown：

```bash
mvn exec:java -Dexec.args="--format markdown src/test/resources/samples/SampleController.java"
```

### 3. 扫描整个目录

默认文本格式：

```bash
mvn exec:java -Dexec.args="src/test/resources/samples"
```

输出 JSON：

```bash
mvn exec:java -Dexec.args="src/test/resources/samples --format json"
```

输出 Markdown：

```bash
mvn exec:java -Dexec.args="src/test/resources/samples --format markdown"
```

或者先打包后直接运行：

```bash
mvn package
java -cp target/classes com.example.javareviewer.cli.JavaReviewerApplication --format json src/test/resources/samples
```

## 输出格式说明

### Text
适合直接在终端阅读，包含：
- 单文件问题列表
- 项目级汇总
- 严重级别统计
- 最差文件列表

### JSON
适合：
- 给脚本或 CI 消费
- 后续接入前端页面或 API
- 做结构化归档

单文件 JSON 包含：
- `reportType`
- `target`
- `totalIssues`
- `severitySummary`
- `issues`

目录 JSON 额外包含：
- `targetDirectory`
- `totalFiles`
- `worstFiles`
- `files`

### Markdown
适合：
- 贴到 PR 评论 / Wiki / 文档平台
- 形成项目巡检报告

当前已支持：
- 单文件 Markdown 报告
- 项目级 Markdown 汇总 + 文件详情

## 输出示例

### 单文件 JSON 示例

```json
{
  "reportType": "single-file",
  "target": "src/test/resources/samples/SampleController.java",
  "totalIssues": 3,
  "severitySummary": {
    "HIGH": 1,
    "MEDIUM": 2,
    "LOW": 0
  }
}
```

### 项目级 Markdown 示例

```markdown
# Java Reviewer Project Report

- **Target directory:** src/test/resources/samples
- **Total files:** 3
- **Total issues:** 8
- **Severity summary:** HIGH=3, MEDIUM=5, LOW=0

## Worst files
- `src/test/resources/samples/SampleTransactionalController.java` — 3 issues
- `src/test/resources/samples/SampleController.java` — 3 issues
```

## 测试与样例

- 测试类：
  - `src/test/java/com/example/javareviewer/JavaFileScannerTest.java`
  - `src/test/java/com/example/javareviewer/cli/JavaReviewerApplicationTest.java`
- 样例代码：
  - `src/test/resources/samples/SampleController.java`
  - `src/test/resources/samples/SampleService.java`
  - `src/test/resources/samples/SampleTransactionalController.java`

## 后续可扩展方向

- 更稳健的 Java AST 解析
- 规则分组、禁用、阈值控制
- Maven / Spring 项目结构自动识别
- AI 增强整改建议
- 输出到文件、SARIF、HTML 等更多格式
