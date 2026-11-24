// DO NOT EDIT: GENERATED FROM kit@6e62ecb9756df2aad5829162f26ebf8840a392ea by tool/vendor (toolVersion 2). Host edits belong outside vendor/kit. See vendor-lock.json.
import { TreeItem } from "./tree-types";

export function collectTreeValues<T>(items: TreeItem<T>[]): T[] {
  const values: T[] = [];
  for (const item of items) {
    if (item.value) {
      values.push(item.value);
    }
    if (item.items?.length) {
      values.push(...collectTreeValues(item.items));
    }
  }
  return values;
}
