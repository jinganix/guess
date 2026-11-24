# kit-adapter（host 侧适配层，非生成物）

`src/vendor/kit/**` 是只读生成物（`DO NOT EDIT`，见根 `vendor-lock.json`），
本目录是宿主在 vendor 区之外写的平台适配层：允许且正规，不叫 `overrides`，
绝不反向修改 `vendor/kit`。

## vendored 单元（kit `6e62ecb9`，toolVersion 2，supportMode buyout）

- `helper-utils`（Java）：`service/.../guess/vendor/kit/helper/utils/**`
 （`KitTimeZone`、`MonthRangeUtils`、`UtilsService`），包前缀已改写为
  `io.github.jinganix.guess.vendor.kit`。
- `helper-enumeration`（Java，kit-adopt 替换宿主副本）：`vendor/kit/helper/enumeration/**`
  （`EnumMapper`、`IntegerEnumMapper`，`StringEnumMapper` 为同包闭包附带，未被宿主引用），
  宿主 `service/helper/enumeration/*` 已删，引用改线到 `vendor.kit`。
- `helper-uid`（Java）：`vendor/kit/helper/uid/UidGenerator.java`，宿主
  `SnowflakeGenerator` 改为实现 vendored 接口；`Snowflake`/`SnowflakeGenerator`/
  `UidUtils` 未 vendor（闭包外宿主已有等价实现 / 需未声明的 `jakarta.annotation`）。
- `helper-auth-token`（Java）：`vendor/kit/helper/auth/token/JwtToken.java`，宿主
  `TokenService`/`JwtTokenService`/`JwtTokenAuthenticator` 改线到 `vendor.kit`。
- `setup-argument`（Java）：`vendor/kit/setup/argument/AnnotationFinder.java`，宿主
  同名副本已删（逻辑与 kit 一致，仅包头不同）。
- `arch-rules`（Java，测试域）：`vendor/kit/arch/KitArchitectureRules.java`（单文件零同包，
  仅依赖 archunit + spring 注解）；宿主新增 `HostArchitectureTest` 全量接线 host-defaults
  规则（faces=`module`，时钟规则用 scoped 过载：`Snowflake.getMillis` 与宿主时钟源除外，
  见测试注释），另修 12 个 handler `@Transactional`、3 处注入命名、时钟改走 `UtilsService`、
  `puzzle/config` 并入 `model`、`MomentListHandler` 掩码逻辑收进 `MomentMapper#mapToListPb`。
  注意 kit 侧 `generic-check` G1 会误报 archunit 自身 `core.domain` 包（工具假阳性，kit 跟进项）。
- `helpers-pure`（TS，依赖零子集）：`src/vendor/kit/helpers/{utils,collection}/**`
  共 11 个文件，见 `src/vendor-lock.json` 的 `files[].originPath`。

## 修剪记录（helpers-pure 白名单全集 `utils/** + collection/**` 通不过 generic-check）

闭包会拖入 `async/mobx-replay`、`hooks/use-infinite-paged-list`（mobx/react），
因此 roots 收敛到文件级零依赖子集；被裁文件及原因：

- `utils/compare-version.ts`：BigInt 字面量需 ES2020，宿主 tsconfig target es6（TS2737）。
- `utils/cn.ts`：宿主已有等价实现（`helpers/utils/cn.ts`），不重复内联。
- `utils/dispose.ts`：`mobx` + `@beankit/all`（G2 UI runtime）。
- `utils/utils.ts`、`collection/unique-sorted.ts`：`lodash-es`（宿主仅声明 `lodash`，第三方门禁不过）。
- `collection/cursor-loader.ts`、`collection/list-scroll-fetch.ts`：`mobx`（G2）。
- `collection/pageable.ts`、`paginate-records.ts`、`table-types.ts`、`client-paged.ts`、
  `client-filter-page.ts`：`@kit-proto` 链（宿主无 `@kit-proto`，第三方门禁不过）。
- `collection/infinite-paged-list-presets.ts`：`hooks/use-infinite-paged-list`（react）。
- `collection/table-column-visibility-storage.ts`：`localStorage`（G2 DOM）。

## wx 平台适配（本目录）

- `index.ts`：vendored 纯逻辑的统一再出口。
- `page-query.ts`：`fromPageOptions(defaults, options)` —— weapp `onLoad(options)`
  是字符串 record（低版本基础库无 `URLSearchParams`），转接给 vendored `fromQuery`。

## 再跑 vendor（维护者，kit 可读时）

```bash
git clone --depth 1 <kit-url> .tmp/code-kit   # 或复用本地 kit 检出
cp -r .tmp/code-kit/tool/vendor .tmp/vendor-tool
# 将本次 roots（见上 + vendor-lock.json files[].originPath）写回 .tmp/vendor-tool/vendor.json
bash .tmp/vendor-tool/vendor.sh helper-utils \
  --target service/guess/src/main/java/io/github/jinganix/guess \
  --kit-dir .tmp/code-kit --package io.github.jinganix.guess
# kit-adopt 新增单元（精确文件 roots；注意 tool 的闭包会对同包/传递导入展开，
# 本次 4 个单元经人工核减为精确文件集后落盘，见 .tmp/kit-adopt/verdicts.tsv；
# 若直接跑 vendor.sh 会因闭包膨胀/第三方门禁搜索根失败，需先修 tool（closure 精确化 +
# 门禁搜模块根，kit 侧 TOOL_VERSION 再 bump）再走标准流程）：
for u in setup-argument helper-auth-token helper-uid helper-enumeration; do
  bash .tmp/vendor-tool/vendor.sh "$u" \
    --target service/guess/src/main/java/io/github/jinganix/guess \
    --kit-dir .tmp/code-kit --package io.github.jinganix.guess
done
# per-target 原生落盘：tool 直接以最终 import 根为 target 写 vendor/、.vendor/、
# vendor-lock.json，无需事后 mv（相对路径即最终路径，hash 稳定）。
bash .tmp/vendor-tool/vendor.sh helpers-pure --target frontend/weapp/src \
  --kit-dir .tmp/code-kit --package io.github.jinganix.guess
# 确定性后处理（tool 跟进项：未合入 code-kit rewrite.sh 前每次 vendor 后必跑）：
# 加 DO NOT EDIT 头 + QuerySource 补丁 + base 镜像 + lock hash 刷新。
bash scripts/vendor-apply-headers.sh
bash .tmp/vendor-tool/lib/verify.sh collect-licenses --host frontend/weapp/src --kit-dir .tmp/code-kit
bash .tmp/vendor-tool/lib/verify.sh collect-licenses --host service/guess/src/main/java/io/github/jinganix/guess --kit-dir .tmp/code-kit
# 合并为根 THIRD-PARTY-NOTICES（dest 前缀子树路径），删除两处子树内副本
```

本地回归（无需 kit）：`bash scripts/verify-vendor.sh`（头 + base==dest + lock hash +
残留 + weapp 全局变量门禁）或前端 `npm run verify:vendor`。

升级唯一路径：维护者重跑 vendor 后重新发包，不在交付物里跑 status/定时 CI。
