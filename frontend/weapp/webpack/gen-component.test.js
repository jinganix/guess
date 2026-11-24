const fs = require("fs");
const os = require("os");
const path = require("path");
const { execFileSync } = require("child_process");
const { checkFile } = require("../scripts/check-wxml-bind");
const { propsFields, scriptFields } = require("../scripts/check-wxml-bind");

const BIN = path.join(__dirname, "..", "scripts", "gen-component.js");

describe("gen-component", () => {
  it("scaffolds a component that passes all checks", () => {
    const out = fs.mkdtempSync(path.join(os.tmpdir(), "gen-comp-"));
    execFileSync("node", [BIN, "tmp-scaffold-check", "--out", out], { stdio: "pipe" });
    const dir = path.join(out, "tmp-scaffold-check");
    for (const f of ["index.ts", "index.wxml", "index.scss", "index.json", "script.ts"]) {
      expect(fs.existsSync(path.join(dir, f))).toBe(true);
    }
    JSON.parse(fs.readFileSync(path.join(dir, "index.json"), "utf-8"));
    expect(checkFile(dir)).toEqual([]);
    const props = propsFields(fs.readFileSync(path.join(dir, "index.ts"), "utf-8"));
    const fields = scriptFields(fs.readFileSync(path.join(dir, "script.ts"), "utf-8"));
    expect([...props].filter((p) => !fields.has(p))).toEqual([]);
    fs.rmSync(out, { force: true, recursive: true });
  });

  it("rejects non-kebab-case names", () => {
    expect(() => execFileSync("node", [BIN, "BadName"], { stdio: "pipe" })).toThrow();
  });
});
