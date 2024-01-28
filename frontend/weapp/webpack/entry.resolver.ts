import { existsSync } from "fs";
import { find, identity, pickBy, startsWith } from "lodash";
import * as globby from "fast-glob";
import * as path from "path";
import { cast, requireFile } from "./utils";
import { App, Component, WebpackConfig } from "./types";

export class EntryResolver {
  constructor(private rootDir: string, private srcDir: string, private config: WebpackConfig) {}

  resolve(): Record<string, string[]> {
    const app: App = requireFile(path.resolve(this.srcDir, "app.json"));
    const pages = this.getPages(app);
    const tabBarDir = path.resolve(this.srcDir, "custom-tab-bar");
    const entries = existsSync(tabBarDir) ? [`${tabBarDir}/index`, ...pages] : pages;
    entries.push(path.resolve(this.srcDir, "app"));
    const components = this.getComponents(entries);
    const data: Record<string, string[]> = pickBy(
      {
        ...this.getEntries(this.srcDir, [this.srcDir + "/*"]),
        ...this.getEntryPoints(pages),
        ...this.getEntryPoints(components),
        sitemap: [path.resolve(this.srcDir, "sitemap.json")],
      },
      identity
    );
    return this.mapEntries(data);
  }

  private getPages(app: App): string[] {
    const { pages = [], subPackages = [] } = app;
    for (const subPackage of subPackages) {
      const pageRoot = subPackage.root;
      for (let page of subPackage.pages) {
        page = pageRoot + page;
        pages.push(page);
      }
    }
    return pages.map((e) => path.resolve(this.srcDir, e));
  }

  private getComponents(entries: string[]): string[] {
    const components: string[] = [...entries];
    entries.forEach((entry) => this.getEntryComponents(entry, components));
    return components;
  }

  private getEntryComponents(entry: string, components: string[]): void {
    const jsonFile = path.resolve(this.rootDir, entry + ".json");
    const data: Component = cast(existsSync(jsonFile) ? require(jsonFile) : {});
    if (data.usingComponents) {
      Object.values(data.usingComponents).forEach((e: string) => {
        let dir = "";
        for (const key in this.config.components) {
          if (e.startsWith(key)) {
            dir = e.replace(key, this.config.components[key]);
          }
        }
        if (!dir) {
          if (startsWith(e, "/components")) {
            dir = path.resolve(this.srcDir, "." + e);
          } else {
            dir = path.resolve(path.dirname(jsonFile), e);
          }
        }
        const nextEntry: string = path.relative(this.rootDir, dir);
        if (!find(components, (e) => e === nextEntry)) {
          components.push(nextEntry);
          this.getEntryComponents(nextEntry, components);
        }
      });
    }
  }

  private getEntries(rootDir: string, patterns: string[]): Record<string, string[]> {
    const fileList = globby.sync(patterns);
    return fileList.reduce((value, current) => {
      const filePath = path.parse(path.relative(this.rootDir, current));
      const entry = path.join(filePath.dir, filePath.name);
      value[entry] || (value[entry] = []);
      value[entry].push(path.resolve(rootDir, current));
      return value;
    }, {} as Record<string, string[]>);
  }

  private getEntryPoints(entries: string[]): Record<string, string[]> {
    const obj = {} as Record<string, string[]>;
    entries.forEach((entry) => {
      const key = path.relative(this.rootDir, entry);
      if (entry.endsWith("/index")) {
        obj[key] = globby.sync(path.resolve(this.rootDir, entry).replace(/\/index$/, "/**"));
      } else {
        obj[key] = globby.sync(path.resolve(this.rootDir, entry) + ".*");
      }
    });
    return obj;
  }

  private mapEntries(data: Record<string, string[]>): Record<string, string[]> {
    const entries: Record<string, string[]> = {};
    for (const key in data) {
      const entry = this.config.entryMappers.mapping(key);
      entries[entry] = data[key].filter((e) => !this.config.exclude(e));
    }
    return entries;
  }
}
