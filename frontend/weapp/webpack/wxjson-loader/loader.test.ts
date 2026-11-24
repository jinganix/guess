import * as webpack from "webpack";
import loader from "./index";
import type { WxJsonLoaderOptions } from "./index";

function runLoader(source: string | Buffer, options: WxJsonLoaderOptions = {}) {
  const added: string[] = [];
  const loaded: string[] = [];
  const ctx = {
    addDependency: (file: string) => {
      added.push(file);
    },
    context: "/root/src",
    getOptions: () => options,
    loadModule: (url: string, callback: (err: Error | null, src?: string) => void) => {
      loaded.push(url);
      callback(null, "");
    },
    query: options,
    resourcePath: "/root/src/app.json",
    rootContext: "/root",
  } as unknown as webpack.LoaderContext<WxJsonLoaderOptions>;
  const result = loader.call(ctx, source);
  return { added, loaded, result };
}

const appJson = JSON.stringify({
  pages: ["pages/index/index"],
  tabBar: {
    list: [
      {
        iconPath: "assets/images/a.png",
        selectedIconPath: "assets/images/b.png",
      },
    ],
  },
});

describe("wxjson-loader", () => {
  it("registers tabBar icons as dependencies", () => {
    const { added, loaded, result } = runLoader(appJson);
    expect(loaded.some((r) => r.endsWith("assets/images/a.png"))).toBe(true);
    expect(loaded.some((r) => r.endsWith("assets/images/b.png"))).toBe(true);
    expect(added).toHaveLength(2);
    expect(result).toBe(appJson);
  });

  it("compacts json with the minify option", () => {
    const pretty = `{\n  "pages": [\n    "a"\n  ]\n}`;
    const { loaded, result } = runLoader(pretty, { minify: true });
    expect(loaded).toHaveLength(0);
    expect(result).toBe(JSON.stringify(JSON.parse(pretty)));
  });

  it("touches nothing when there is no tabBar", () => {
    const source = JSON.stringify({ pages: ["a"] });
    const { loaded, result } = runLoader(source);
    expect(loaded).toHaveLength(0);
    expect(result).toBe(source);
  });

  it("throws on invalid json instead of emitting garbage", () => {
    expect(() => runLoader("{oops")).toThrow();
  });
});
