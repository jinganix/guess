// Webpack loader: implicitly bind wxml template fields to the script.
// Rewrites the first `super(comp)` (no explicit picks) in `script.ts` into
// `super(comp, [...])`, with picks derived from the sibling `index.wxml`.
// Files without a match, with explicit picks, with a missing/unparsable wxml,
// or under FULL_BIND are passed through: the runtime then binds everything
// (see ComponentScript), so correctness never depends on this loader.
const fs = require("fs");
const path = require("path");
const { getOptions } = require("loader-utils");
const { extractPicks } = require("./extract");

const SUPER_CALL = /super\s*\(\s*comp\s*\)/;

module.exports = function loader(source) {
  const options = (typeof this.getOptions === "function" ? this.getOptions() : getOptions(this)) || {};
  const callback = this.async();
  if (options.fullBind || process.env.FULL_BIND) {
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
  let picks;
  try {
    picks = extractPicks(fs.readFileSync(wxmlPath, "utf-8"));
  } catch (error) {
    this.emitWarning(
      new Error(`[pick-loader] ${path.relative(process.cwd(), wxmlPath)}: ${error.message}; falling back to full binding`),
    );
    callback(null, source);
    return;
  }
  if (typeof this.addDependency === "function") {
    this.addDependency(wxmlPath);
  }
  callback(null, source.replace(SUPER_CALL, `super(comp, ${JSON.stringify(picks)})`));
};
