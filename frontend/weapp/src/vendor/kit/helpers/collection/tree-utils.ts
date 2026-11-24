// DO NOT EDIT: GENERATED FROM kit@c32c22eb4ddd729c36ecc8ee2c28362a54bf417a by tool/vendor (toolVersion 1). Host edits belong outside vendor/kit. See vendor-lock.json.
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
