# 隐式 Pick 绑定（B 方案）Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 页面/组件绑定字段不再靠 `npm run pick` 预生成 `pick.ts` 落盘，构建时由 webpack loader 从同目录 `index.wxml` 隐式推导并注入；业务代码里看不到该机制；一个开关可切全量绑定。

**Architecture:** 新增 `webpack/pick-loader/`（纯 JS：`extract.js` 解析 + `index.js` loader 改写 `super(comp)`）；运行时 `ComponentScript/Observer` 的 `picks` 改为可选，`undefined` 即全量绑定（兜底 + jest 下默认行为）；删掉 `pick.js`、全部 `pick.ts`、`import { PICKS }`。

**Tech Stack:** webpack loader (plain Node JS + acorn, 已安装), TypeScript, jest, mobx（运行时不动 API，只放宽参数）。

**Spec:** 本 plan 即 spec（用户原话：B 方案 + 隐式绑定 + 全量开关 + 修正分析问题 + 完善测试）。

## Global Constraints

- 业务 `script.ts` 最终形态：`constructor(comp) { super(comp); }`，无任何 pick 相关 import/参数。
- `test: /[\\/]script\.ts$/` 的 loader rule 必须排除 `helpers/wx/component.script.ts`（`.` 前缀，非路径分隔符，天然排除；改动后复查）。
- 生产构建行为与今天一致：对全部 26 个现有 `index.wxml`，loader 输出必须逐字节等于已提交的 `pick.ts`（Task 1 的 golden-check 钉住，删文件前跑）。
- 不新增 npm 依赖（`acorn`、`loader-utils` 已有）。
- 全量开关：构建期环境变量 `FULL_BIND=1`（loader 跳过注入 → 运行时全量）。Windows 用 `set FULL_BIND=1 && npm run build`。

## Review Focus

- `super(comp)` 正则改写误伤：在 `script.ts` 里出现第二个 `super(comp)`（如未来新增）会被一并改写——loader 只改写第一处，测试钉住。
- `index.wxml` 缺失/表达式不可解析时 loader 静默跳过（warning + 全量兜底），不出错构建——每个分支有测试。
- `options[item]` 这类计算属性：`item` 变化时视图是否刷新——`extract` 同时收 `options` 与 `item`，测试钉住。
- `icon-follow` 等组件的 `item` 是 script observable，不能按 `wx:for` 局部变量排除——保持现状（全收），注释说明。

## File Structure

- Create: `frontend/weapp/webpack/pick-loader/extract.js` — 纯函数：`extractPicks(wxml: string): string[]`、`extractWxsModules` 内聚其中。
- Create: `frontend/weapp/webpack/pick-loader/index.js` — loader：读同目录 `index.wxml`，改写首个 `super(comp)`，`addDependency(wxml)`，`FULL_BIND` 跳过。
- Create: `frontend/weapp/webpack/pick-loader/extract.test.js`, `frontend/weapp/webpack/pick-loader/loader.test.js` (+ `__fixtures__/`).
- Create: `frontend/weapp/src/helpers/wx/observer.test.ts` — picks=undefined 深观察行为。
- Modify: `frontend/weapp/src/helpers/wx/observer.ts`（`picks?` 可选）、`frontend/weapp/src/helpers/wx/component.script.ts`（`picks?` + 全量 `defaultData`）。
- Modify: `frontend/weapp/webpack/webpack.config.ts`（+rule）、`frontend/weapp/package.json`（删 `pick` 及两处 `npm run pick`）、`frontend/weapp/jest.config.js`（testMatch +webpack）。
- Modify: 26 个 `src/**/script.ts`（删 import + `, PICKS`）。
- Delete: `frontend/weapp/pick.js`、全部 `src/**/pick.ts`（26 个）。

## Interfaces

- `extractPicks(wxmlContent: string): string[]` — 抛错当且仅当某 `{{}}` 内表达式 acorn 解析失败（loader 捕获转 warning + 全量兜底）。
- loader 改写：`super(comp)` → ``super(comp, ["a","a.b"])``（`JSON.stringify(picks)`，无空格，与现有格式一致即可，语义相同）。
- `ComponentScript(comp, picks?: string[])`；`Observer(listener, picks?: string[])`；`undefined` = 全量。

---

### Task 1: 抽取逻辑 `extract.js`（含分析问题修正）

**Files:** Create `frontend/weapp/webpack/pick-loader/extract.js`; Test `frontend/weapp/webpack/pick-loader/extract.test.js`.

**Interfaces:** Consumes: 无（纯函数，依赖 `acorn`）。Produces: `extractPicks(wxml: string): string[]`（排序去重；失败抛 `Error("...expression...")`）供 Task 2 loader 用。

修正点（相对老 `pick.js`，每条有测试）：
1. mustache 正则支持跨行：`/{{([\s\S]*?)}}/g`。
2. 先剥 `<!--...-->` 注释再扫。
3. 解析 `<wxs module="x">` 声明，根名为 x 的标识/成员路径一律排除。
4. JS 全局 `undefined/NaN/Infinity` 作 Identifier 时排除。
5. 计算属性非字面 key：同时收录（`options[item]`→`options`+`item`；`options[item.key]`→`options`+`item.key`；`options[0]`→`options`；`options['a']`→`options.a`）。
6. 任一表达式解析失败 → 抛错（不再 console.error 后吞掉给不完整结果）。
7. 保持现状：`wx:for` 的 `item/index` 不排除（本仓库 `item` 多为 script observable，见 icon-follow/icon-like；注释说明）。

- [ ] **Step 1: Write the failing test** — `extract.test.js`，用例：avatar 三元+`||`（`iconClass/rootClass/user.avatar`）、`clsx([...], iconClass)` 只收 `iconClass`、`content.length`、`options[item]`→`options`+`item`、`options[0]`→`options`、`options['a']`→`options.a`、`x === undefined`→`x`、`m.format(x)`+`<wxs module="m">`→`x`、跨行 `{{...}}`、注释内 `{{...}}` 忽略、非法表达式抛错、排序去重。

```js
expect(extractPicks(`<view>{{user.avatar || '../../a.png'}}</view>`)).toEqual(["user.avatar"]);
expect(extractPicks(`<view>{{options[item]}}</view>`)).toEqual(["item", "options"]);
expect(() => extractPicks(`<view>{{a %% b}}</view>`)).toThrow();
```

- [ ] **Step 2: Run test to verify it fails** — Run: `cd frontend/weapp && npx jest webpack/pick-loader/extract.test.js` Expected: FAIL（module not found）。
- [ ] **Step 3: Implement `extractPicks` in `frontend/weapp/webpack/pick-loader/extract.js`** — 从老 `pick.js` 迁移 `extractAccessExpressions/getMemberExpressionString` 并应用上述 7 条修正。
- [ ] **Step 4: Run test to verify it passes** — Run: 同上 Expected: PASS。
- [ ] **Step 5: Golden-check（一次性，不入库）** — 写 `/tmp/golden-check.js` 对全部 26 个 `src/**/index.wxml` 跑 `extractPicks` 并与已提交 `pick.ts`逐个对比。Expected: 26/26 一致（`edit` 的 `options[item]` 因 `item` 已存在而不产生 diff）。把结果记入本 plan 末尾（append 一行）。
- [ ] **Step 6: Commit** — `git add frontend/weapp/webpack/pick-loader/ && git commit -m "feat(frontend): add wxml picks extractor with analysis fixes"`

### Task 2: loader `index.js`（隐式注入 + 开关）

**Files:** Create `frontend/weapp/webpack/pick-loader/index.js`; Test `frontend/weapp/webpack/pick-loader/loader.test.js` + `__fixtures__/{basic,wxml-missing,explicit-picks,unparsable}/`（fixture 为成对 `script.ts` 样板 + `index.wxml`，放 loader 目录下，EntryResolver 已排除 `*.test.*`，fixture 命名避开 `*.test.*` 且不在 src 下，不会被打包）。

**Interfaces:** Consumes: Task 1 的 `extractPicks`。Produces: webpack loader（`module.exports = function (source)`），行为见步骤。

- [ ] **Step 1: Write the failing test** — mock `this`（`resourcePath/async/emitWarning/addDependency/query`），用例：
  1. `super(comp)` + 同目录 wxml 有绑定 → 首个 `super(comp)` 被改写为 `super(comp, [...])`，且 `addDependency` 被以 wxml 路径调用；
  2. 已有 `super(comp, ["a"])` → 原样返回；
  3. 同目录无 `index.wxml` → 原样返回（运行时全量兜底）；
  4. wxml 含非法表达式 → `emitWarning` 被调用且原样返回；
  5. `options.fullBind: true` 或 `FULL_BIND=1` → 原样返回；
  6. 两个 `super(comp)`（假设未来）→ 只改写第一处。

```js
const out = runLoader("constructor(comp) { super(comp); }", fixtureDir);
expect(out).toContain(`super(comp, ["show"])`);
```

- [ ] **Step 2: Run test to verify it fails** — Run: `npx jest webpack/pick-loader/loader.test.js` Expected: FAIL。
- [ ] **Step 3: Implement loader** — 要点：`getOptions(this)` 取 `fullBind`；`process.env.FULL_BIND` 同效；`fs.existsSync(wxml)` 缺失则 passthrough；`try extractPicks catch → emitWarning + passthrough`；正则只替换第一处 `/super\s*\(\s*comp\s*\)/`。
- [ ] **Step 4: Run test to verify it passes** — Run: 同上 Expected: PASS。另跑 Task 1 测试确保仍绿。
- [ ] **Step 5: Commit** — `git commit -m "feat(frontend): inject wxml picks at build time via pick-loader"`

### Task 3: 运行时 `picks?` + 全量兜底

**Files:** Modify `frontend/weapp/src/helpers/wx/observer.ts`, `frontend/weapp/src/helpers/wx/component.script.ts`; Test `frontend/weapp/src/helpers/wx/observer.test.ts`.

**Interfaces:** Consumes: 无新增。Produces: `Observer(listener, picks?: string[])`、`ComponentScript(comp, picks?: string[])`；`picks === undefined` 时 `defaultData()` 返回全部顶层 observable、`observe()` 递归全部嵌套。

- [ ] **Step 1: Write the failing test** — `observer.test.ts`：`makeObservable` 对象 `{ user: observable({avatar: "a"}) }`，`new Observer(cb)`（无 picks）→ 改 `user.avatar` → `cb` 以 `("user", {avatar:"b"})` 被调用；`new Observer(cb, ["user.avatar"])` 同样成立（回归）；`defaultData` 级别：构造测试用 `ComponentScript` 子类（comp 传 null），`defaultData()` 含全部 observable key。
- [ ] **Step 2: Run test to verify it fails** — Run: `npx jest src/helpers/wx/observer.test.ts` Expected: FAIL（TS 类型错：picks 必填 / 行为不符）。
- [ ] **Step 3: Implement** — `observer.ts`: `picks?: string[]`，`this.picks = picks?.map(String)`，`observe()` 内 `const includes = this.picks ? ... : undefined`；`component.script.ts`: 构造函数 `picks?: string[]`，`defaultData()` 用 `this._picks ?? getObservableKeys(this)`。约 5 行。
- [ ] **Step 4: Run tests** — `npx jest src/helpers/wx/` Expected: 全绿（含既有 `pick.obervable.test.ts`）。
- [ ] **Step 5: Commit** — `git commit -m "feat(frontend): support full binding when picks absent"`

### Task 4: 接线 + 删除旧机制

**Files:** Modify `webpack.config.ts`、`package.json`、`jest.config.js`、26 个 `script.ts`; Delete `pick.js` + 26 个 `pick.ts`.

- [ ] **Step 1: 接线** — webpack.config 新增 rule（置于 ts rule 之后）：`{ include: [SRC_DIR], test: /[\\/]script\.ts$/, use: [pickLoader()] }` + helper；`package.json` 删 `"pick"` 及 `build/dev` 中的 `npm run pick && `；`jest.config.js` testMatch 加 `"<rootDir>/webpack/**/*.{spec,test}.{js,ts}"`。
- [ ] **Step 2: 删旧机制** — `sed` 去掉 26 个 `script.ts` 的 `import { PICKS } from "./pick";` 行与 `, PICKS` 参数；`git rm pick.js src/**/pick.ts`。
- [ ] **Step 3: 静态验证** — Run: `npx tsc --noEmit -p tsconfig.json` Expected: 0 error；Run: `npx eslint src webpack/pick-loader` Expected: 0 error（若有 unused import 告警就地修）。
- [ ] **Step 4: 构建验证** — Run: `npx webpack --config webpack/webpack.config.ts --mode development --env dev`（或 `npm run dev` 一次性）Expected: 成功；`grep -o 'super(comp, \[[^]]*\])' dist/components/avatar/index.js`…实际是编译后代码，改查：`grep -c "user.avatar" dist/components/avatar/index.js` Expected: ≥1（注入生效）；另 `FULL_BIND=1` 构建一次，`grep -c "user.avatar"` Expected: 0（开关生效）。
- [ ] **Step 5: 全量测试** — Run: `cd frontend/weapp && npx jest` Expected: 全绿。
- [ ] **Step 6: Commit** — `git commit -m "feat(frontend): implicit wxml picks binding via webpack loader"`

### Task 5: 最终自检（verification-before-completion）

- [ ] `git status` 确认无 `pick.ts`/`pick.js` 残留、无业务代码引用 `PICKS`（`grep -rn PICKS src webpack` 只剩 loader/测试/observer 内部命名）。
- [ ] 全套件 `npx jest` 再跑一次并记录结果。
- [ ] 按 superpowers:requesting-code-review 走一次自查（无 subagent 时自己读 diff），结论记入回复。

## Self-Review

- Spec 覆盖：隐式（Task 2+4）✓；开关（Task 2+4-Step4）✓；分析修正（Task 1 七条）✓；测试（每 task 附测试 + 全量套件）✓。
- `wx:for` 局部变量保持收录：是刻意决定（Review Focus 第 4 条），非遗漏。
- 类型一致：`extractPicks` 纯 JS（`string[]` 以 JSDoc 标注）；运行时 `picks?: string[]` 两处签名一致。
- 篇幅：plan 约等于代码量，未写实现体，OK。

## Execution Log

- Task 1 golden-check（删文件前）：26 个 `src/**/pick.ts`（含 `more-options` 空数组，共 27 个 pick.ts？实为 27 个文件 27/27）逐个对比 `extractPicks(index.wxml)`，27/27 一致。唯一语义增强：`options[item].length` 在老逻辑丢 `item`，新逻辑保留（`edit` 页因另有 `options[item]` 本来就含 `item`，无 diff）。

## Execution Log (review fix pass)

- Reviewer（fresh Agent）结论已重定级并处理：`addDependency` 前移到 parse 之前（dev 下修 wxml 也能触发重编）✓；`String.replace` 改 replacement 函数（`{{a$$b}}` 实测塌过）✓；rule 加 `enforce: "pre"`（不再依赖跨 rule 顺序假设）✓；`FULL_BIND` 严格判断仅 `1/true`（`FULL_BIND=0` 不再生效）✓；新增 `golden.test.js` snapshot 把 27 个真实 wxml 的抽取结果钉在仓库里（脚本核对与基线旧 pick.ts 27/27 一致）✓。
- 不修（Minor→deferred）：`super(comp)` 正则可能命中注释/字符串、多 component 同文件只改第一处；wxs `module = "x"` 空格写法漏检；`}}` 在字符串字面量里截断（安全方向：warning+全量）；计算 key 里 `options[fmt(x)]` 会多收 `fmt`（无害未知名）；`[]` 与 `undefined` 在 Observer 等价但在 defaultData 不等价（保留老 quirk）；`webpack/pick-loader/*.js` 不在 `eslint src` 门禁内（沿袭老 pick.js）；`script.tsx`/改名构造参数会静默全量。
- 预先存在、与本分支无关：`src/pages/puzzle/detail/scripts.test.ts` 在基线 `f7de263` 同样失败（container↔comment-list 循环依赖），本分支 44/44 其余全绿。
