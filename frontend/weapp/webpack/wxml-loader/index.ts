import * as sax from "sax";
import { Context, Script } from "vm";
import * as Minifier from "html-minifier";
import { getOptions, isUrlRequest, urlToRequest } from "loader-utils";
import { startsWith } from "lodash";
import * as path from "path";
import * as webpack from "webpack";
import { cast, moduleLoader } from "../utils";

interface Request {
  endIndex: number;
  request: string;
  startIndex: number;
}

interface Options {
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

function getPublicPath(ctx: Record<string, unknown>): string {
  const property = "publicPath";
  const options: { output: Record<string, string> } = cast(ctx["options"]);
  if (options && options.output && property in options.output) {
    return options.output[property];
  }
  const compilation: { outputOptions: Record<string, string> } = cast(ctx["_compilation"]);
  if (compilation && compilation.outputOptions && property in compilation.outputOptions) {
    return compilation.outputOptions[property];
  }
  return "";
}

export default function loader(
  this: webpack.LoaderContext<Options>,
  source: string | Buffer,
): string | Buffer | void | undefined {
  let content = source.toString();
  const callback = this.async();
  const { resourcePath, context } = this;

  const options: Options = getOptions(cast(this)) || {};
  const root = (options.root as string) ?? path.resolve(context);
  const publicPath = (options.publicPath as string) ?? getPublicPath(cast(this));
  const enforceRelativePath = (options.enforceRelativePath as boolean) ?? false;
  const minimize = (options.minimize as boolean) ?? false;

  const requests: Request[] = [];

  const ensureStartsWithDot = (source: string): string =>
    startsWith(source, ".") ? source : `./${source}`;

  const ensureRelativePath = (source: string): string => {
    const sourcePath = path.join(root, source);
    const resourceDirname = path.dirname(resourcePath);
    source = path.relative(resourceDirname, sourcePath).replace(/\\/g, "/");
    return ensureStartsWithDot(source);
  };

  const replaceRequest = async ({ request, startIndex, endIndex }: Request): Promise<void> => {
    const module: string = await moduleLoader.loadModule(this, request);
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

  parser.onattribute = ({ name, value }): void => {
    if (!value || name != "src" || /{{/.test(value) || !isUrlRequest(value, root)) {
      return;
    }

    const endIndex: number = parser.position - 1;
    const startIndex: number = endIndex - value.length;
    const request: string = urlToRequest(value, root);

    requests.unshift({ endIndex, request, startIndex });
  };

  parser.onend = (): void => {
    try {
      void Promise.all(requests.map((request) => replaceRequest(request))).then(() => {
        if (minimize) {
          content = Minifier.minify(content, {
            ...defaultMinimizeConf,
          });
        }
        callback(null, content);
      });
    } catch (err) {
      callback(err as Error, content);
    }
  };

  parser.write(content).close();
}
