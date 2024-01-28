import { getOptions, interpolateName } from "loader-utils";
import * as webpack from "webpack";
import { cast } from "../utils";

interface Options {
  context?: string;
  mapOutput?: (url: string, resourcePath: string, context: string) => string;
  name?: string;
  regExp?: string;
}

export default function loader(
  this: webpack.LoaderContext<unknown>,
  source: string | Buffer,
  sourceMap?: string
): string | Buffer | void | undefined {
  const options: Options = getOptions(cast(this)) || {};
  const context = options.context || this.rootContext;
  let url = interpolateName(cast(this), options.name ?? "[path][name].[ext]", {
    context,
    regExp: options.regExp,
    source,
  });

  if (options.mapOutput) {
    url = options.mapOutput(url, this.resourcePath, context);
  }

  this.emitFile(url, source, sourceMap);
  return source;
}
