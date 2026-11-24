import { interpolateName } from "loader-utils";
import * as webpack from "webpack";
import { cast } from "../utils";

export interface DumpLoaderOptions {
  context?: string;
  mapOutput?: (url: string, resourcePath: string, context: string) => string;
  name?: string;
  regExp?: string;
}

export default function loader(
  this: webpack.LoaderContext<DumpLoaderOptions>,
  source: string | Buffer,
  sourceMap?: string,
): string | Buffer {
  const options = this.getOptions() || {};
  const context = options.context || this.rootContext;
  // @types/loader-utils still models the webpack 4 context: narrow the call site only.
  let url = interpolateName(cast(this), options.name ?? "[path][name].[ext]", {
    content: source,
    context,
    regExp: options.regExp,
  });

  if (options.mapOutput) {
    url = options.mapOutput(url, this.resourcePath, context);
  }

  this.emitFile(url, source, sourceMap);
  return source;
}
