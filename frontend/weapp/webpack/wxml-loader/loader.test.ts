import * as path from "path";
import * as webpack from "webpack";
import loader from "./index";
import type { WxmlLoaderOptions } from "./index";

function runLoader(
  source: string,
  dir: string,
  files: Record<string, string | Error>,
  options: WxmlLoaderOptions = {},
  delays: Record<string, number> = {},
): Promise<{ content: string | Buffer | void; requested: string[] }> {
  const requested: string[] = [];
  const ctx = {
    _compilation: { outputOptions: { publicPath: "/" } },
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
      const wait = delays[name] ?? 0;
      const file = files[name];
      const respond = () => {
        if (file === undefined || file instanceof Error) {
          callback(file instanceof Error ? file : new Error(`missing ${url}`));
        } else {
          callback(null, file);
        }
      };
      if (wait > 0) {
        setTimeout(respond, wait);
      } else {
        respond();
      }
    },
    query: options,
    resourcePath: path.join(dir, "index.wxml"),
  } as unknown as webpack.LoaderContext<WxmlLoaderOptions>;
  let resolve!: (content: string | Buffer | void) => void;
  let reject!: (err: Error) => void;
  const done = new Promise<string | Buffer | void>((res, rej) => {
    resolve = res;
    reject = rej;
  });
  loader.call(ctx, source);
  return done.then((content) => ({ content, requested }));
}

describe("wxml-loader", () => {
  it("rewrites a static src with the bundled output", async () => {
    const { content, requested } = await runLoader(
      '<view><image src="./a.png" /></view>',
      "/test/wxml-a",
      { "a.png": 'module.exports = "./assets/a.png";' },
    );
    expect(requested.some((r) => r.endsWith("a.png"))).toBe(true);
    expect(content).toBe('<view><image src="./assets/a.png" /></view>');
  });

  it("skips mustache bindings and absolute urls", async () => {
    const source = '<image src="{{icon}}" /><image src="https://x/y.png" />';
    const { content, requested } = await runLoader(source, "/test/wxml-b", {});
    expect(requested).toHaveLength(0);
    expect(content).toBe(source);
  });

  it("replaces every src correctly when resolutions finish out of order", async () => {
    const { content } = await runLoader(
      '<image src="./a.png"/><image src="./very-long-name.png"/>',
      "/test/wxml-c",
      {
        "a.png": 'module.exports = "s1";',
        "very-long-name.png": 'module.exports = "s2";',
      },
      {},
      { "very-long-name.png": 30 },
    );
    expect(content).toBe('<image src="./s1"/><image src="./s2"/>');
  });

  it("rewrites src in every top-level node, not just the first", async () => {
    const { content } = await runLoader(
      '<image src="./a.png"/><image src="./b.png"/>',
      "/test/wxml-f",
      {
        "a.png": 'module.exports = "./a1.png";',
        "b.png": 'module.exports = "./b1.png";',
      },
    );
    expect(content).toBe('<image src="./a1.png"/><image src="./b1.png"/>');
  });

  it("rewrites every occurrence of the same src", async () => {
    const { content } = await runLoader(
      '<image src="./a.png"/><image src="./a.png"/>',
      "/test/wxml-g",
      { "a.png": 'module.exports = "./dup.png";' },
    );
    expect(content).toBe('<image src="./dup.png"/><image src="./dup.png"/>');
  });

  it("removes comments with the minimize option", async () => {
    const { content } = await runLoader(
      "<!-- hi --><view>a</view>",
      "/test/wxml-d",
      {},
      { minimize: true },
    );
    expect(String(content)).not.toContain("<!--");
    expect(String(content)).toContain("<view>a</view>");
  });

  it("fails the build when a referenced file is missing", async () => {
    await expect(
      runLoader('<image src="./missing.png" />', "/test/wxml-e", {}),
    ).rejects.toThrow(/missing/);
  }, 2000);
});
