import * as sax from "sax";
import { Context, Script } from "vm";
import { minify } from "html-minifier-terser";
import { isUrlRequest, urlToRequest } from "loader-utils";
import { startsWith } from "lodash";
import * as path from "path";
import * as webpack from "webpack";
import { moduleLoader } from "../utils";

interface Request {
  endIndex: number;
  request: string;
  startIndex: number;
}

export interface WxmlLoaderOptions {
  enforceRelativePath?: boolean;
  minimize?: boolean;
  publicPath?: string;
  root?: string;
}

const extract = (src: string, __webpack_public_path__: string): string => {
  const script = new Script(src);
  const sandbox = {
    __webpack_public_path__,
    module: {},
  } as Context;
  script.runInNewContext(sandbox);
  return sandbox.module.exports.toString();
};

const defaultMinimizeConf = {
  caseSensitive: true,
  collapseWhitespace: true,
  html5: true,
  keepClosingSlash: true,
  removeCDATASectionsFromCDATA: true,
  removeComments: true,
  removeCommentsFromCDATA: true,
  removeEmptyAttributes: true,
  removeRedundantAttributes: true,
  removeScriptTypeAttributes: true,
  removeStyleLinkTypeAttributes: true,
};

function getPublicPath(ctx: webpack.LoaderContext<WxmlLoaderOptions>): string {
  const publicPath = ctx._compilation?.outputOptions?.publicPath;
  return typeof publicPath === "string" ? publicPath : "";
}

export default function loader(
  this: webpack.LoaderContext<WxmlLoaderOptions>,
  source: string | Buffer,
): void {
  let content = source.toString();
  const callback = this.async();
  const { resourcePath, context } = this;

  const options = this.getOptions() || {};
  const root = options.root ?? path.resolve(context);
  const publicPath = options.publicPath ?? getPublicPath(this);
  const enforceRelativePath = options.enforceRelativePath ?? false;
  const minimize = options.minimize ?? false;

  const requests: Request[] = [];

  const ensureStartsWithDot = (source: string): string =>
    startsWith(source, ".") ? source : `./${source}`;

  const ensureRelativePath = (source: string): string => {
    const sourcePath = path.join(root, source);
    const resourceDirname = path.dirname(resourcePath);
    source = path.relative(resourceDirname, sourcePath).replace(/\\/g, "/");
    return ensureStartsWithDot(source);
  };

  const applyModule = (module: string, { startIndex, endIndex }: Request): void => {
    if (!module) {
      return;
    }
    let source = extract(module, publicPath);
    const isSourceAbsolute = path.isAbsolute(source);
    if (!isSourceAbsolute && !/^(\w+:)?\/\//.test(source)) {
      source = ensureStartsWithDot(source);
    }
    if (enforceRelativePath && isSourceAbsolute) {
      source = ensureRelativePath(source);
    }
    content = content.slice(0, startIndex) + source + content.slice(endIndex);
  };

  const parser = sax.parser(false, { lowercase: true });

  // WXML is a fragment: sax stops after the first top-level node, so wrap
  // the content in a synthetic root and subtract its length from positions.
  const wrapperOpen = "<root>";
  const positionOffset = wrapperOpen.length;

  parser.onattribute = ({ name, value }): void => {
    if (!value || name != "src" || /{{/.test(value) || !isUrlRequest(value, root)) {
      return;
    }

    const endIndex: number = parser.position - 1 - positionOffset;
    const startIndex: number = endIndex - value.length;
    const request: string = urlToRequest(value, root);

    requests.unshift({ endIndex, request, startIndex });
  };

  parser.onend = (): void => {
    (async () => {
      // Load in parallel, then apply back-to-front in document order so
      // earlier replacements never shift the recorded indices of later ones.
      // Requests are stored in reverse document order: applying them as
      // stored keeps every slice aligned. The map reuses one load for
      // repeated src values, which ModuleLoader would otherwise dedupe to "".
      const unique = [...new Set(requests.map(({ request }) => request))];
      const loaded = await Promise.all(
        unique.map((request) => moduleLoader.loadModule(this, request)),
      );
      const modules = new Map(unique.map((request, i) => [request, loaded[i]]));
      for (const { request, startIndex, endIndex } of requests) {
        applyModule(modules.get(request) ?? "", { endIndex, request, startIndex });
      }
      if (minimize) {
        content = await minify(content, {
          ...defaultMinimizeConf,
        });
      }
      callback(null, content);
    })().catch((err: Error) => callback(err, content));
  };

  parser.write(`${wrapperOpen}${content}</root>`).close();
}
