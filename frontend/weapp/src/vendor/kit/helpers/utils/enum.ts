// DO NOT EDIT: GENERATED FROM kit@c32c22eb4ddd729c36ecc8ee2c28362a54bf417a by tool/vendor (toolVersion 1). Host edits belong outside vendor/kit. See vendor-lock.json.
export interface Option<T> {
  label: string;
  value: T;
}

/**
 * Numeric-enum reverse lookup narrowed to the member-name union.
 *
 * `Enum[value]` is typed `string`, so it cannot index lang key unions directly.
 * This helper narrows it to `Extract<keyof typeof Enum, string>` for typed
 * `t`/`tk` index access.
 */
export function enumName<E extends Record<string, string | number>>(
  enumObject: E,
  value: number,
): Extract<keyof E, string> {
  return enumObject[value] as Extract<keyof E, string>;
}

/** Numeric members of an enum object (drops reverse-lookup keys and string members). */
export function enumValues<T>(enumObj: unknown): T {
  return Object.values(enumObj as { [s: string]: unknown }).filter(
    (value) => typeof value === "number",
  ) as T;
}

/** Resolve a numeric or string enum value to its string key name. */
export function enumKey(
  enumObj: Record<string | number, string | number>,
  value: string | number | null | undefined,
): string | undefined {
  if (value == null) {
    return undefined;
  }
  if (typeof value === "string") {
    if (value in enumObj && typeof enumObj[value] === "number") {
      return value;
    }
    const asNumber = Number(value);
    if (!Number.isNaN(asNumber) && asNumber in enumObj) {
      const key = enumObj[asNumber];
      return typeof key === "string" ? key : undefined;
    }
    return undefined;
  }
  const key = enumObj[value];
  return typeof key === "string" ? key : undefined;
}

/** Build localized options from a numeric enum + its typed label pack (`t.enum.xxx`). */
export function enumToOptions<T extends object>(
  obj: T,
  labels: Record<string, string>,
): Option<number>[] {
  return Object.values(obj)
    .filter((x) => typeof x === "string")
    .map((x) => {
      return {
        label: labels[x] ?? x,
        value: (obj as Record<string, number>)[x],
      };
    });
}

/** Invert a generated const-enum `*ByName` map into a value→name lookup. */
export function invertEnumNameMap(
  byName: Record<string, number>,
): Record<number, string> {
  const names: Record<number, string> = {};
  for (const [name, value] of Object.entries(byName)) {
    names[value] = name;
  }
  return names;
}

/** Build `Option`s for a const enum from its `*Values` array + value→name map + typed member-name labels. */
export function constEnumOptions<T extends number>(
  values: readonly T[],
  names: Record<number, string>,
  labels: Record<string, string>,
): Option<T>[] {
  return values.map((value) => ({
    label: labels[names[value]] ?? names[value],
    value,
  }));
}

/** Build `Option`s from a numeric-keyed enum lang pack (`t.enum.xxx`, `Partial<Record<Enum, string>>`; missing text falls back to the numeric value). */
export function enumValueOptions<T extends number>(
  values: readonly T[],
  labels: Partial<Record<T, string>>,
): Option<T>[] {
  return values.map((value) => ({
    label: labels[value] ?? String(value),
    value,
  }));
}
