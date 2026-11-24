# frontend/weapp — AI 开发约定

原生微信小程序 + 自研 webpack 构建链。改代码前先读完这一页。

## 组件 5 件套（`src/components/<name>/`，页面同理）

| 文件 | 管什么 | AI 注意 |
|---|---|---|
| `script.ts` | 唯一逻辑：`class XScript extends ComponentScript`，`@observable accessor` 即视图字段 | 只在这里写逻辑；构造函数保持 `super(comp)`，**不要手写 picks 参数**（构建时 `pick-loader` 从 `index.wxml` 隐式注入） |
| `index.ts` | `Component(defaultComponent(XScript, x => new XScript(x), { prop: {type, value} }))` | properties 的 key 集合必须 = script 的 observable 字段集合（`props-consistency.test.ts` 会拦） |
| `index.wxml` | `{{字段}}` 绑定，只做展示 | 逻辑（拼接/条件）收到 script 的 getter 里；`wx:for` 的 `item` 等局部变量不要当字段用 |
| `index.scss` | tailwind 类 + 少量自定义 | 见下 |
| `index.json` | `usingComponents` 注册 | 引用新组件必须在这里注册，路径用相对路径 |

## 禁止

- 禁止 `setData`：响应式走 mobx observable + `Observer`，直接 `setData` 会绕过它。
- 禁止手写 `super(comp, [...])`：picks 由构建注入，手写会被覆盖且 jest 下行为不一致（jest 无 loader，全量绑定）。
- 禁止绝对路径引用组件：`wxml-loader` 要求相对路径。

## 样式

tailwind 可用，但经 `weapp-tailwindcss` + `postcss-rem-to-responsive-pixel` 转 rpx。不要手写 `px` 定死宽高；`assets/` 图片走相对路径。

## 状态/数据

跨组件：`@modules/container` 的 `cacheService/components/userStore`。同页组件通信用 `components.getInPage(XScript, this.pageId)`（见 comment/script.ts 范例）。网络/model 层在 `src/modules/`，不要在组件里直接 wx.request。

## 改完跑什么（一个命令）

```shell
cd frontend/weapp
npm run check   # tsc --noEmit + eslint + wxml 绑定校验
npm test        # jest（只覆盖 ts 逻辑，wxml 不进覆盖率）
node scripts/gen-component.js foo-bar  # 新建组件脚手架
```

CI 收紧：`STRICT_PICK=1 npm run build` 让 `pick-loader` 在 wxml 表达式不可解析时直接失败（默认只 warning 兜底全量绑定）。

WXML 校验（`scripts/check-wxml-bind.js`）：`index.wxml` 的 `{{}}` 字段必须能在同目录 `script.ts`（observable/getter）/`index.ts`（properties）/wxs 模块名中找到，找不到就报错。`wx:for` 局部变量与 `wxs` 模块名已白名单。
