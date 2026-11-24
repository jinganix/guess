import { urlToRequest } from "loader-utils";
import * as path from "path";
import * as webpack from "webpack";
import { moduleLoader } from "../utils";

export interface WxJsonLoaderOptions {
  minify?: boolean;
}

interface TabBar {
  list: { iconPath: string; selectedIconPath: string }[];
}

export default function loader(
  this: webpack.LoaderContext<WxJsonLoaderOptions>,
  source: string | Buffer,
): string | Buffer {
  const options = this.getOptions() || {};

  const text = source.toString();
  const parsed = JSON.parse(text) as { tabBar?: TabBar };
  const addAsset = (icon: string): void => {
    const request = urlToRequest(path.relative(this.rootContext, icon));
    void moduleLoader.loadModule(this, request);
  };
  parsed.tabBar?.list?.forEach((e) => {
    e.iconPath && addAsset(e.iconPath);
    e.selectedIconPath && addAsset(e.selectedIconPath);
  });

  return options.minify ? JSON.stringify(parsed) : source;
}
