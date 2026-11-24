import * as fs from "fs";
import * as path from "path";
import { extractPicks } from "./extract";

function collectWxml(dir: string, out: string[] = []): string[] {
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      collectWxml(full, out);
    } else if (entry.name === "index.wxml") {
      out.push(full);
    }
  }
  return out;
}

describe("real wxml bindings", () => {
  it("keeps extraction stable across all src templates", () => {
    const srcDir = path.join(__dirname, "..", "..", "src");
    const result: Record<string, string[]> = {};
    for (const file of collectWxml(srcDir).sort()) {
      result[path.relative(srcDir, path.dirname(file))] = extractPicks(
        fs.readFileSync(file, "utf-8"),
      );
    }
    expect(result).toMatchSnapshot();
  });
});
