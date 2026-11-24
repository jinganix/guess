// DO NOT EDIT: GENERATED FROM kit@c32c22eb4ddd729c36ecc8ee2c28362a54bf417a by tool/vendor (toolVersion 1). Host edits belong outside vendor/kit. See vendor-lock.json.
export interface TreeItem<T> {
  label: string;
  value: T;
  code: string;
  items?: TreeItem<T>[];
}
