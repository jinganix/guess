import * as webpack from "webpack";
import * as path from "path";

export function cast<T>(obj: unknown): T {
  return obj as T;
}

export function requireFile<T>(path: string): T {
  // eslint-disable-next-line @typescript-eslint/no-var-requires
  return cast(require(path));
}

export interface PathMapper {
  mapping: (entry: string) => string;

  supports: (entry: string) => boolean;
}

// Collects all matches of a pattern without leaking lastIndex state:
// each call runs on a fresh RegExp, so module-level /g patterns are safe.
export function collectMatches(text: string, pattern: RegExp): RegExpExecArray[] {
  const flags = pattern.flags.includes("g") ? pattern.flags : `${pattern.flags}g`;
  const regex = new RegExp(pattern.source, flags);
  const matches: RegExpExecArray[] = [];
  let match: RegExpExecArray | null;
  while ((match = regex.exec(text)) !== null) {
    matches.push(match);
  }
  return matches;
}

export class PathMappers {
  constructor(private mappers: PathMapper[]) {}

  mapping(entry: string): string {
    for (const mapper of this.mappers) {
      if (mapper.supports(entry)) {
        return mapper.mapping(entry);
      }
    }
    return entry;
  }
}

export class ModuleLoader {
  private loaded: { [key: string]: boolean } = {};

  addEntries(entries: Record<string, string[]> = {}): void {
    for (const key in entries) {
      for (const entry of entries[key]) {
        this.loaded[entry] = true;
      }
    }
  }

  loadModule(ctx: webpack.LoaderContext<unknown>, request: string): Promise<string> {
    return new Promise<string>((resolve, reject) => {
      const url = path.resolve(ctx.context, request);
      if (!this.loaded[url]) {
        this.loaded[url] = true;
        ctx.addDependency(url);
        ctx.loadModule(url, (err, src) => (err ? reject(err) : resolve(String(src))));
      } else {
        resolve("");
      }
    });
  }
}

export const moduleLoader: ModuleLoader = new ModuleLoader();
