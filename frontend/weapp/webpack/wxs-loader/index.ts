import { getOptions, urlToRequest } from "loader-utils";
import * as Terser from "terser";
import * as webpack from "webpack";
import { cast, moduleLoader } from "../utils";

export default function loader(
  this: webpack.LoaderContext<unknown>,
  source: string | Buffer,
  sourceMap?: string,
): string | Buffer | void | undefined {
  const options = getOptions(cast(this)) || {};
  let text = source.toString();

  const callback = this.async();
  const regex = /require[\s('"]+(.+?\.wxs)[\s)'"]+/g;
  const promises = [];
  do {
    const match = regex.exec(text);
    if (!match) {
      break;
    }
    const request = urlToRequest(match[1]);
    promises.push(moduleLoader.loadModule(this, request));
  } while (true);
  void Promise.all(promises).then(() => {
    if (!options.minify) {
      callback(null, text, sourceMap);
      return;
    }
    const regex = /getRegExp\((.*)\)[,;]/g;
    const values: string[] = [];
    let i = 0;
    do {
      const match = regex.exec(text);
      if (!match) {
        break;
      }
      const value = match[1];
      text = text.replace(value, `__PLACEHOLDER_${i}__`);
      values[i++] = value;
    } while (true);
    void Terser.minify(text, {
      compress: false,
      format: {
        braces: true,
        comments: false,
        semicolons: true,
      },
    }).then((value) => {
      text = (value.code ?? "").replace(/{return}/g, "{return;}");
      for (let i = 0; i < values.length; i++) {
        text = text.replace(`__PLACEHOLDER_${i}__`, values[i].replace("$", "$$$$"));
      }
      callback(null, text, sourceMap);
    });
  });
}
