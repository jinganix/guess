import { urlToRequest } from "loader-utils";
import * as Terser from "terser";
import * as webpack from "webpack";
import { collectMatches, moduleLoader } from "../utils";

export interface WxsLoaderOptions {
  minify?: boolean;
}

export default function loader(
  this: webpack.LoaderContext<WxsLoaderOptions>,
  source: string | Buffer,
  sourceMap?: string,
): void {
  const options = this.getOptions() || {};
  let text = source.toString();

  const callback = this.async();
  const promises: Promise<string>[] = [];
  for (const match of collectMatches(text, /require[\s('"]+(.+?\.wxs)[\s)'"]+/g)) {
    promises.push(moduleLoader.loadModule(this, urlToRequest(match[1])));
  }
  Promise.all(promises).then(
    () => {
      if (!options.minify) {
        callback(null, text, sourceMap);
        return;
      }
      const values: string[] = [];
      let i = 0;
      for (const match of collectMatches(text, /getRegExp\((.*)\)[,;]/g)) {
        const value = match[1];
        text = text.replace(value, `__PLACEHOLDER_${i}__`);
        values[i++] = value;
      }
      Terser.minify(text, {
        compress: false,
        format: {
          braces: true,
          comments: false,
          semicolons: true,
        },
      }).then(
        (value) => {
          text = (value.code ?? "").replace(/{return}/g, "{return;}");
          for (let i = 0; i < values.length; i++) {
            // Escape every `$`: values are re-inserted via String.replace,
            // which treats `$` patterns specially.
            text = text.replace(`__PLACEHOLDER_${i}__`, values[i].replace(/\$/g, "$$$$"));
          }
          callback(null, text, sourceMap);
        },
        (err: Error) => callback(err),
      );
    },
    (err: Error) => callback(err),
  );
}
