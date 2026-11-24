import * as path from "path";
import * as webpack from "webpack";
import loader from "./index";
import type { DumpLoaderOptions } from "./index";

function runLoader(
  source: string | Buffer,
  resourcePath: string,
  options: DumpLoaderOptions = {},
) {
  const emitted: { content: string | Buffer; url: string }[] = [];
  const ctx = {
    context: path.dirname(resourcePath),
    emitFile: (url: string, content: string | Buffer) => {
      emitted.push({ content, url });
    },
    getOptions: () => options,
    query: options,
    resourcePath,
    rootContext: "/root",
  } as unknown as webpack.LoaderContext<DumpLoaderOptions>;
  const result = loader.call(ctx, source);
  return { emitted, result };
}

describe("dump-loader", () => {
  it("emits the file and returns the source unchanged", () => {
    const source = "<view>hi</view>";
    const { emitted, result } = runLoader(source, "/root/src/pages/index/index.wxml", {
      context: "/root",
    });
    expect(emitted).toHaveLength(1);
    expect(emitted[0].url).toBe("src/pages/index/index.wxml");
    expect(emitted[0].content).toBe(source);
    expect(result).toBe(source);
  });

  it("maps the output url with mapOutput", () => {
    const { emitted } = runLoader("x", "/root/src/pages/index/index.wxml", {
      context: "/root",
      mapOutput: (url) => url.replace("src/", ""),
    });
    expect(emitted[0].url).toBe("pages/index/index.wxml");
  });

  it("keeps buffer content as-is", () => {
    const source = Buffer.from([0x89, 0x50, 0x4e, 0x47]);
    const { emitted, result } = runLoader(source, "/root/src/assets/a.png", {
      context: "/root",
    });
    expect(emitted[0].content).toBe(source);
    expect(result).toBe(source);
  });
});
