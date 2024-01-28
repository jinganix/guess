import { getOptions, urlToRequest } from "loader-utils";
import * as path from "path";
import * as webpack from "webpack";
import { cast, moduleLoader } from "../utils";

interface TabBar {
  list: [{ iconPath: string; selectedIconPath: string }];
}

export default function loader(
  this: webpack.LoaderContext<unknown>,
  source: string | Buffer
): string | Buffer | void | undefined {
  const options = getOptions(cast(this)) || {};

  const text = source.toString();
  const assets: string[] = [];
  const { tabBar }: { tabBar: TabBar } = cast(JSON.parse(text));
  if (tabBar) {
    tabBar.list &&
      tabBar.list.forEach((e: { iconPath: string; selectedIconPath: string }) => {
        e.iconPath && assets.push(e.iconPath);
        e.selectedIconPath && assets.push(e.selectedIconPath);
      });
  }

  assets.filter(Boolean).forEach((e) => {
    const request = urlToRequest(path.relative(this.rootContext, e));
    void moduleLoader.loadModule(this, request);
  });

  return options.minify ? JSON.stringify(JSON.parse(source.toString())) : source;
}
