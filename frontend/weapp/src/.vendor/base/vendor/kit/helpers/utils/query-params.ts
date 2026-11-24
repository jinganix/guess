// DO NOT EDIT: GENERATED FROM kit@6e62ecb9756df2aad5829162f26ebf8840a392ea by tool/vendor (toolVersion 2). Host edits belong outside vendor/kit. See vendor-lock.json.
/** Minimal query-source read surface for fromQuery (host patch: `URLSearchParams`
 * narrowed to `{ has, get }` so weapp `onLoad` records adapt without a cast; a real
 * `URLSearchParams` still satisfies this structurally). Upstream: adopt in kit source. */
export interface QuerySource {
  has(key: string): boolean;
  get(key: string): string | null;
}

/** Overlay URL query params onto a defaults object (numbers/booleans coerced). */
export function fromQuery<T>(t: T, source?: QuerySource): T {
  if (!source) {
    return t;
  }
  const target = Object.assign({}, t as Record<string, unknown>);
  for (const key of Object.keys(target)) {
    const value = target[key];
    if (typeof value === "number") {
      if (source.has(key) && value !== Number(source.get(key))) {
        target[key] = Number(source.get(key));
      }
    } else if (typeof value === "boolean") {
      if (source.has(key) && value !== (source.get(key) === "true")) {
        target[key] = source.get(key) === "true";
      }
    } else if (source.has(key) && value !== source.get(key)) {
      target[key] = source.get(key);
    }
  }
  return target as T;
}
