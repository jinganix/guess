import { Environment } from "./env/env";
import { PathMappers } from "./utils";

export interface App {
  pages: string[];
  subPackages: SubPackage[];
  tabBar: TabBar;
  usingComponents: { [key: string]: string };
  window: unknown;
}

export interface Component {
  usingComponents: { [key: string]: string };
}

export interface SubPackage {
  root: string;
  pages: string[];
}

export interface TabBar {
  custom: boolean;
}

export interface WebpackConfig {
  components: Record<string, string>;

  entryMappers: PathMappers;

  environment: Environment;

  exclude: (path: string) => boolean;

  fileMappers: PathMappers;
}
