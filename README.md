# java-reviewer

一个面向 **Java / Spring 项目** 的代码审查 CLI 原型。当前版本已经不只是“扫几个 Java 文件”，而是开始具备 **Spring 项目结构识别 + 分层味道检查 + 整改建议输出** 能力。

## 已实现能力

### CLI / 扫描能力
- Maven + Java 21 标准项目结构
- CLI 入口：支持 **单文件** 与 **目录** 扫描
- CLI 参数：支持 `--format text|json|markdown`
- 包结构：`cli`, `scanner`, `rules`, `report`, `model`
- 文本、JSON、Markdown 报告输出

### Spring 项目结构识别
目录扫描时会基于 **路径 / 类名 / 注解** 做启发式识别，并在报告中输出以下角色统计：
- `controller`
- `service`
- `repository`
- `entity`
- `config`
- `util`
- `other`

适合快速判断一个 Spring 项目大致分层是否清晰、结构是否完整。

### 当前规则
#### 已有规则
- Controller 直接依赖 Repository
- Service 泄漏 Web 对象（`HttpServletRequest/Response`, `Model`, `ModelMap`, `WebRequest`）
- `System.out/System.err print/println/printf` 输出替代提醒（建议改为结构化日志）
- `printStackTrace()` 调用检查
- `@Transactional` 放在 Controller 或 private/final/static 方法上的风险提示
- `catch(Exception/Throwable)` 过宽捕获提醒
- `catch(Exception/Throwable)` 吞异常 / 返回默认值检查
- Controller 方法过多、职责可能过重提醒

#### 本轮新增规则
- **LoggingWithoutExceptionObjectRule**：`catch` 中打日志但未传入异常对象，导致堆栈信息丢失
- **ControllerBusinessLogicRule**：Controller 中出现循环、计算、校验、直接仓储访问等业务逻辑味道
- **ServiceWriteMethodWithoutTransactionalRule**：Service 中疑似写操作方法缺少清晰事务边界
- **RepositoryNamingAndResponsibilityRule**：Repository/DAO 命名不统一，或持久层混入 Web / 业务职责

### 项目级能力
- 统计总文件数
- 统计总问题数
- 按严重级别汇总（HIGH / MEDIUM / LOW）
- 输出最差文件列表
- 输出 Spring 项目结构识别结果
- 输出“**整改建议清单**”（按优先级汇总 top issues / recommended actions）
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
mvn exec:java -Dexec.args="src/test/resources/spring-structure-demo"
```

输出 JSON：

```bash
mvn exec:java -Dexec.args="src/test/resources/spring-structure-demo --format json"
```

输出 Markdown：

```bash
mvn exec:java -Dexec.args="src/test/resources/spring-structure-demo --format markdown"
```

或者先打包后直接运行：

```bash
mvn package
java -cp target/classes com.example.javareviewer.cli.JavaReviewerApplication --format markdown src/test/resources/spring-structure-demo
```

## 输出格式说明

### Text
适合直接在终端阅读，包含：
- 单文件问题列表
- 项目级汇总
- 严重级别统计
- Spring 结构摘要
- 最差文件列表
- 整改建议清单

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
- `springStructureSummary`
- `recommendedActions`
- `worstFiles`
- `files`

### Markdown
适合：
- 贴到 PR 评论 / Wiki / 文档平台
- 形成项目巡检报告

当前已支持：
- 单文件 Markdown 报告
- 项目级 Markdown 汇总 + Spring 结构摘要 + 整改建议 + 文件详情

## 输出示例

### 项目级 Markdown 示例

```markdown
# Java Reviewer Project Report

- **Target directory:** src/test/resources/spring-structure-demo
- **Total files:** 6
- **Total issues:** 7
- **Severity summary:** HIGH=1, MEDIUM=5, LOW=1
- **Spring structure summary:** controller=1, service=1, repository=1, entity=1, config=1, util=1, other=0

## Recommended actions
- **[MEDIUM] LoggingWithoutExceptionObjectRule** (issues: 1) — 统一改为结构化日志，记录关键信息并在异常场景传入 throwable。
- **[MEDIUM] ControllerBusinessLogicRule** (issues: 1) — 把循环、校验、计算、聚合等业务逻辑从 Controller 提炼到 Service。
```

## 测试与样例

### 测试类
- `src/test/java/com/example/javareviewer/JavaFileScannerTest.java`
- `src/test/java/com/example/javareviewer/cli/JavaReviewerApplicationTest.java`

### 样例代码
基础样例：
- `src/test/resources/samples/SampleController.java`
- `src/test/resources/samples/SampleService.java`
- `src/test/resources/samples/SampleTransactionalController.java`

Spring 结构 / 分层味道样例：
- `src/test/resources/spring-structure-demo/controller/OrderController.java`
- `src/test/resources/spring-structure-demo/service/OrderService.java`
- `src/test/resources/spring-structure-demo/repository/OrderDao.java`
- `src/test/resources/spring-structure-demo/entity/OrderEntity.java`
- `src/test/resources/spring-structure-demo/config/AppConfig.java`
- `src/test/resources/spring-structure-demo/util/DateUtil.java`

## 后续可扩展方向

- 更稳健的 Java AST 解析
- 规则分组、禁用、阈值控制
- 输出到文件、SARIF、HTML 等更多格式
- 增加基于包层级与注解组合的 Spring 架构审查
- 接入 CI / PR 注释 / 质量门禁
