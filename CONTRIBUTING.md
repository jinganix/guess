# Contributing

本仓库使用 [Conventional Commits](https://www.conventionalcommits.org/)。

## 格式

```
<type>(<scope>): <subject>

[可选 body，说明 why 而非 what]
```

- **subject**：祈使句、简短；首行（header）总长 ≤ 100 字符
- **scope**：必填，见下表
- **语言：仅英文（ASCII）** — header 与 body 禁止中文 / 非 ASCII（`validate-commit-msg.sh` 强制）
- 合并 PR 时标题也应符合同一格式

## type

| type | 何时用 |
|------|--------|
| `feat` | 新功能、新模块 |
| `fix` | 缺陷修复 |
| `docs` | 仅文档 |
| `style` | 格式调整，不改变语义 |
| `refactor` | 结构调整，不改变对外语义 |
| `perf` | 性能相关 |
| `test` | 仅测试 |
| `build` | 构建、Gradle、依赖版本 |
| `ci` | CI、git hooks |
| `chore` | 杂项维护 |
| `revert` | 回滚某次提交 |

## scope（本仓库）

| scope | 路径 / 内容 |
|-------|-------------|
| `service` | `service/guess`（后端） |
| `frontend` | `frontend/weapp`（小程序前端） |
| `proto` | `proto/`（proto 定义） |
| `build` | `build.gradle.kts`、`buildSrc/`、`settings.gradle.kts`、Gradle wrapper、依赖版本升级 |
| `ci` | `.githooks/`、`docs/scripts/`、`.github/` |
| `docs` | `README.md`、`docs/`、`CONTRIBUTING.md` |
| `deps` | 依赖升级（renovate） |

跨多个 scope 的大改可拆成多次提交；单次提交选**最主要**的 scope。

### 示例

```
feat(service): add guess result cache

fix(frontend): normalize error codes in HttpService

docs(docs): document frontend import aliases in README
```

## 本地校验（shell hooks，无需 npm）

克隆或更新后执行一次：

```bash
./docs/scripts/setup-git-hooks.sh
```

试跑：

```bash
echo "feat(service): test message" | ./docs/scripts/validate-commit-msg.sh /dev/stdin
```

**提交/推送门禁**：`pre-commit` 与 `pre-push` 都会执行 `docs/scripts/check.sh`，确保**后端编译通过、格式检查通过、前端 lint 与单测通过**：

```bash
./docs/scripts/check.sh
```

紧急绕行（仅限确有理由时，CI 下禁用）：`SKIP_CHECKS=1 ./docs/scripts/check.sh`

直接提交/推送到 `master` 会被拒绝；`.env.local`与`application-local.yml`（本地真实密钥）即使强行 `git add -f` 也会被拒绝。
