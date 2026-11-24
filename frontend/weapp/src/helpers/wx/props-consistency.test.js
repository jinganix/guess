// 组件 index.ts properties 必须在 script.ts 里有同名 observable/getter，
// 否则 property 传进来无人接收（静默丢失）。复用 check-wxml-bind 的提取引擎。
// eslint-disable-next-line @typescript-eslint/no-require-imports
const fs = require("fs");
// eslint-disable-next-line @typescript-eslint/no-require-imports
const path = require("path");
// eslint-disable-next-line @typescript-eslint/no-require-imports
const { propsFields, scriptFields } = require("../../../scripts/check-wxml-bind");

const compsDir = path.join(__dirname, "..", "..", "components");
const names = fs
  .readdirSync(compsDir, { withFileTypes: true })
  .filter((e) => e.isDirectory())
  .map((e) => e.name);

describe.each(names)("%s: properties ⊆ script fields", (name) => {
  it("every property has a matching observable or getter", () => {
    const dir = path.join(compsDir, name);
    const indexSrc = fs.readFileSync(path.join(dir, "index.ts"), "utf-8");
    if (!/defaultComponent\(/.test(indexSrc)) {
      return;
    }
    const scriptPath = path.join(dir, "script.ts");
    if (!fs.existsSync(scriptPath)) {
      return;
    }
    const fields = scriptFields(fs.readFileSync(scriptPath, "utf-8"));
    const missing = [...propsFields(indexSrc)].filter((p) => !fields.has(p));
    expect(missing).toEqual([]);
  });
});
