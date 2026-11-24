import * as path from "path";
import * as webpack from "webpack";
import { collectMatches, ModuleLoader, PathMappers } from "./utils";

describe("PathMappers", () => {
  it("applies the first matching mapper", () => {
    const mappers = new PathMappers([
      {
        mapping: (entry) => entry.replace("src/", ""),
        supports: (entry) => entry.startsWith("src/"),
      },
      {
        mapping: (entry) => `other/${entry}`,
        supports: () => true,
      },
    ]);
    expect(mappers.mapping("src/pages/index")).toBe("pages/index");
  });

  it("returns the entry unchanged when nothing matches", () => {
    const mappers = new PathMappers([
      {
        mapping: (entry) => `other/${entry}`,
        supports: () => false,
      },
    ]);
    expect(mappers.mapping("src/pages/index")).toBe("src/pages/index");
  });
});

describe("collectMatches", () => {
  it("returns every match with groups", () => {
    const matches = collectMatches("a1 b22 c333", /(\w+?)(\d+)/g);
    expect(matches.map((m) => [m[1], m[2]])).toEqual([
      ["a", "1"],
      ["b", "22"],
      ["c", "333"],
    ]);
  });

  it("works with a non-global pattern", () => {
    expect(collectMatches("ab", /a/)).toHaveLength(1);
  });

  it("starts fresh on every call", () => {
    const pattern = /a(\d)/g;
    expect(collectMatches("a1 a2", pattern)).toHaveLength(2);
    expect(collectMatches("a1 a2", pattern)).toHaveLength(2);
  });
});

describe("ModuleLoader", () => {
  function fakeContext(dir: string, sources: Record<string, string>) {
    const ctx = {
      addDependency: jest.fn(),
      context: dir,
      loadModule: (url: string, callback: (err: Error | null, src?: string) => void) => {
        const name = path.basename(url);
        if (sources[name] === undefined) {
          callback(new Error(`missing ${url}`));
        } else {
          callback(null, sources[name]);
        }
      },
    } as unknown as webpack.LoaderContext<unknown>;
    return ctx;
  }

  it("loads a module once and returns empty on repeats", async () => {
    const loader = new ModuleLoader();
    const ctx = fakeContext("/test/a", { "a.wxs": "content-a" });
    await expect(loader.loadModule(ctx, "./a.wxs")).resolves.toBe("content-a");
    await expect(loader.loadModule(ctx, "./a.wxs")).resolves.toBe("");
  });

  it("propagates load errors", async () => {
    const loader = new ModuleLoader();
    const ctx = fakeContext("/test/b", {});
    await expect(loader.loadModule(ctx, "./missing.wxs")).rejects.toThrow(/missing/);
  });

  it("addEntries marks entries as already loaded", async () => {
    const loader = new ModuleLoader();
    const ctx = fakeContext("/test/c", { "e.js": "x" });
    loader.addEntries({ page: [path.resolve("/test/c", "e.js")] });
    await expect(loader.loadModule(ctx, "./e.js")).resolves.toBe("");
  });
});
