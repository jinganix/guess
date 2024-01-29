import * as webpack from "webpack";
import { getOptions, urlToRequest } from "loader-utils";
import { cast, moduleLoader } from "../utils";

export default function loader(
  this: webpack.LoaderContext<unknown>,
  source: string | Buffer,
  sourceMap?: string,
): string | Buffer | void | undefined {
  const options = getOptions(cast(this)) || {};
  const text = source.toString();
  if (options.rpx) {
    return text.replace(/(\d+)px/gm, "$1rpx");
  }

  const callback = this.async();
  const regex = /@import[\s'"]+(.+?\.wxss)[\s)'"]+;/g;
  const promises = [];
  do {
    const match = regex.exec(text);
    if (!match) {
      break;
    }
    const request = urlToRequest(match[1]);
    promises.push(moduleLoader.loadModule(this, request));
  } while (true);
  void Promise.all(promises).then(() => callback(null, text, sourceMap));
  return source;
}
