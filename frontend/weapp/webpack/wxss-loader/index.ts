import * as webpack from "webpack";
import { urlToRequest } from "loader-utils";
import { collectMatches, moduleLoader } from "../utils";

export interface WxssLoaderOptions {
  rpx?: boolean;
}

export default function loader(
  this: webpack.LoaderContext<WxssLoaderOptions>,
  source: string | Buffer,
  sourceMap?: string,
): string | void {
  const options = this.getOptions() || {};
  const text = source.toString();
  if (options.rpx) {
    return text.replace(/(\d+)px/gm, "$1rpx");
  }

  const callback = this.async();
  const promises: Promise<string>[] = [];
  for (const match of collectMatches(text, /@import[\s'"]+(.+?\.wxss)[\s)'"]+;/g)) {
    promises.push(moduleLoader.loadModule(this, urlToRequest(match[1])));
  }
  Promise.all(promises).then(
    () => callback(null, text, sourceMap),
    (err: Error) => callback(err),
  );
}
