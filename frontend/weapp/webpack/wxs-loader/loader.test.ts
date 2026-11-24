import * as path from "path";
import * as webpack from "webpack";
import loader from "./index";
import type { WxsLoaderOptions } from "./index";

function runLoader(
  source: string,
  dir: string,
  files: Record<string, string | Error>,
  options: WxsLoaderOptions = {},
): Promise<{ content: string | Buffer | void; requested: string[] }> {
  const requested: string[] = [];
  const ctx = {
    addDependency: jest.fn(),
    async: () => (err?: Error | null, content?: string | Buffer) => {
      if (err) {
        reject(err);
      } else {
        resolve(content);
      }
    },
    context: dir,
    getOptions: () => options,
    loadModule: (url: string, callback: (err: Error | null, src?: string) => void) => {
      requested.push(url);
      const name = path.basename(url);
      const file = files[name];
      if (file === undefined || file instanceof Error) {
        callback(file instanceof Error ? file : new Error(`missing ${url}`));
      } else {
        callback(null, file);
      }
    },
    query: options,
    resourcePath: path.join(dir, "index.wxs"),
  } as unknown as webpack.LoaderContext<WxsLoaderOptions>;
  let resolve!: (content: string | Buffer | void) => void;
  let reject!: (err: Error) => void;
  const done = new Promise<string | Buffer | void>((res, rej) => {
    resolve = res;
    reject = rej;
  });
  loader.call(ctx, source);
  return done.then((content) => ({ content, requested }));
}

describe("wxs-loader", () => {
  it("registers required wxs files and returns the text unchanged", async () => {
    const source = "var u = require('./util.wxs');\nmodule.exports = { u: u };\n";
    const { content, requested } = await runLoader(source, "/test/wxs-a", {
      "util.wxs": "module.exports = {};",
    });
    expect(requested.some((r) => r.endsWith("util.wxs"))).toBe(true);
    expect(content).toBe(source);
  });

  it("minifies while preserving getRegExp arguments", async () => {
    const source =
      'var re = getRegExp("^[a-z]+$");\nvar x = 1; // strip me\nmodule.exports = { re: re, x: x };\n';
    const { content } = await runLoader(source, "/test/wxs-b", {}, { minify: true });
    expect(String(content)).toContain('"^[a-z]+$"');
    expect(String(content)).not.toContain("strip me");
    expect(String(content)).not.toContain("__PLACEHOLDER");
  });

  it("preserves every $ in getRegExp arguments", async () => {
    const source = 'var re = getRegExp("a$b$&");\nmodule.exports = { re: re };\n';
    const { content } = await runLoader(source, "/test/wxs-c", {}, { minify: true });
    expect(String(content)).toContain('"a$b$&"');
    expect(String(content)).not.toContain("__PLACEHOLDER");
  });

  it("fails the build when a required file is missing", async () => {
    const source = "var u = require('./missing.wxs');\nmodule.exports = { u: u };\n";
    await expect(runLoader(source, "/test/wxs-d", {})).rejects.toThrow(/missing/);
  }, 2000);

  it("fails the build when minification throws", async () => {
    const source = "var = {{{;\n";
    await expect(runLoader(source, "/test/wxs-e", {}, { minify: true })).rejects.toThrow();
  }, 2000);
});
