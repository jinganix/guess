export function pickObservable(source: unknown, paths: string[]): Record<string, unknown> {
  if (
    source === null ||
    source === undefined ||
    Array.isArray(source) ||
    typeof source !== "object"
  ) {
    return {};
  }
  const target = {} as Record<string, unknown>;
  for (const path of paths) {
    let tmpSource: unknown = source;
    let tmpTarget = target;
    const parts = path.split(".");
    for (let i = 0; i < parts.length; i++) {
      const key = parts[i];
      tmpSource = (tmpSource as Record<string, unknown>)[key];
      if (tmpSource === undefined) {
        break;
      }
      if (Array.isArray(tmpSource) || tmpSource == null || typeof tmpSource !== "object") {
        tmpTarget[key] = tmpSource;
        break;
      }
      if (i === parts.length - 1) {
        tmpTarget[key] = tmpSource;
      } else if (!tmpTarget[key]) {
        tmpTarget[key] = {};
      }
      tmpTarget = tmpTarget[key] as Record<string, unknown>;
    }
  }
  return target;
}
