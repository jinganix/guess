import * as path from "path";
import * as webpack from "webpack";
import loader from "./index";
import type { WxssLoaderOptions } from "./index";

function runLoader(
  source: string,
  dir: string,
  files: Record<string, string | Error>,
  options: WxssLoaderOptions = {},
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
    resourcePath: path.join(dir, "index.wxss"),
  } as unknown as webpack.LoaderContext<WxssLoaderOptions>;
  let resolve!: (content: string | Buffer | void) => void;
  let reject!: (err: Error) => void;
  const done = new Promise<string | Buffer | void>((res, rej) => {
    resolve = res;
    reject = rej;
  });
  loader.call(ctx, source);
  return done.then((content) => ({ content, requested }));
}

describe("wxss-loader", () => {
  it("converts px to rpx with the rpx option", () => {
    // Sync path: no async context needed beyond options.
    const ctx = {
      getOptions: () => ({ rpx: true }),
      query: { rpx: true },
    } as unknown as webpack.LoaderContext<WxssLoaderOptions>;
    expect(loader.call(ctx, ".a{width:100px;height:2px}")).toBe(".a{width:100rpx;height:2rpx}");
  });

  it("registers imported wxss files and returns the text unchanged", async () => {
    const source = "@import './base.wxss';\n.a{color:red}";
    const { content, requested } = await runLoader(source, "/test/wxss-a", {
      "base.wxss": ".b{color:blue}",
    });
    expect(requested.some((r) => r.endsWith("base.wxss"))).toBe(true);
    expect(content).toBe(source);
  });

  it("fails the build when an imported file is missing", async () => {
    const source = "@import './missing.wxss';\n.a{color:red}";
    await expect(runLoader(source, "/test/wxss-b", {})).rejects.toThrow(/missing/);
  }, 2000);
});
