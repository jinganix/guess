# docs

项目文档索引。根目录入口：`README.md`（中文）、`README.en.md`（英文）、`CONTRIBUTING.md`（提交规范）。

## 目录

- `qrcode.jpg` — 小程序体验二维码，被两份 README 的“扫码体验”引用。
- `devtools.setting.png` / `devtools.setting.en.png` — 微信开发者工具“关闭域名校验”截图，中英文 README 各用一张。
- `scripts/` — git hooks 与提交门禁脚本（`check.sh`、`setup-git-hooks.sh`、`validate-commit-msg.sh`），由 `.githooks/` 调用，新克隆后跑一次 `./docs/scripts/setup-git-hooks.sh`。
- `superpowers/` — AI 协作过程产物：`specs/` 放设计、`plans/` 放实施计划，仅供追溯，不作为使用文档。

## frontend/weapp 构建/校验命令索引（以 `frontend/weapp/package.json` 实际 scripts 为准）

- `npm install` — 安装依赖。
- `npm start`（等价于 `npm run dev`）— `clean + proto + webpack --watch development`，本地联调。
- `npm run build` — `lint + clean + proto + webpack production`，生产构建。
- `npm run lint` / `npm run lint:fix` — `eslint src` 检查 / 自动修复。
- `npx tsc --noEmit` — TypeScript 类型检查。
- `npm test` — `jest` 全量测试。
- `npm run clean` — 清空 `build/`、`dist/`。
- `npm run proto` — 用 `webpb` 按 `package.json` 内路径生成 proto 代码。

注意：`npm run check` 目前在 `package.json` 中不存在，不要使用；一切以 `package.json` 实际 scripts 为准。
