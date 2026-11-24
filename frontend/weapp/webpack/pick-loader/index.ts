// Webpack loader: implicitly bind wxml template fields to the script.
// Rewrites the first `super(comp)` (no explicit picks) in `script.ts` into
// `super(comp, [...])`, with picks derived from the sibling `index.wxml`.
// Files without a match, with explicit picks, with a missing/unparsable wxml,
// or under FULL_BIND are passed through: the runtime then binds everything
// (see ComponentScript), so correctness never depends on this loader.
import * as fs from "fs";
import * as path from "path";
import * as webpack from "webpack";
import { extractPicks } from "./extract";

export interface PickLoaderOptions {
  fullBind?: boolean;
  strictPick?: boolean;
}

const SUPER_CALL = /super\s*\(\s*comp\s*\)/;

function fullBindEnabled(options: PickLoaderOptions): boolean {
  if (options.fullBind) {
    return true;
  }
  const flag = (process.env.FULL_BIND ?? "").toLowerCase();
  return flag === "1" || flag === "true";
}

function strictPickEnabled(options: PickLoaderOptions): boolean {
  if (options.strictPick) {
    return true;
  }
  const flag = (process.env.STRICT_PICK ?? "").toLowerCase();
  return flag === "1" || flag === "true";
}

export default function loader(
  this: webpack.LoaderContext<PickLoaderOptions>,
  source: string,
): void {
  const options = this.getOptions() || {};
  const callback = this.async();
  if (fullBindEnabled(options)) {
    callback(null, source);
    return;
  }
  if (!SUPER_CALL.test(source)) {
    callback(null, source);
    return;
  }
  const wxmlPath = path.join(path.dirname(this.resourcePath), "index.wxml");
  if (!fs.existsSync(wxmlPath)) {
    callback(null, source);
    return;
  }
  if (typeof this.addDependency === "function") {
    this.addDependency(wxmlPath);
  }
  let picks: string[];
  try {
    picks = extractPicks(fs.readFileSync(wxmlPath, "utf-8"));
  } catch (error) {
    const message = error instanceof Error ? error.message : String(error);
    const err = new Error(
      `[pick-loader] ${path.relative(process.cwd(), wxmlPath)}: ${message}; falling back to full binding`,
    );
    if (strictPickEnabled(options)) {
      callback(err);
      return;
    }
    this.emitWarning(err);
    callback(null, source);
    return;
  }
  const injected = `super(comp, ${JSON.stringify(picks)})`;
  // Replacement function: picks may contain `$` (e.g. `a$$b`),
  // which String.replace would interpret as a pattern.
  callback(null, source.replace(SUPER_CALL, () => injected));
}
