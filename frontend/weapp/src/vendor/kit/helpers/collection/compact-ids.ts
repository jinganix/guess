// DO NOT EDIT: GENERATED FROM kit@c32c22eb4ddd729c36ecc8ee2c28362a54bf417a by tool/vendor (toolVersion 1). Host edits belong outside vendor/kit. See vendor-lock.json.
/**
 * Drop ids that must not reach the server. A table row with no id (`pb.id ?? ""`)
 * otherwise turns a whole delete batch into `ids:[""]`, which the server rejects —
 * so deleting 100 rows fails because one of them had no id.
 */
export function compactIds(
  ids: readonly (string | null | undefined)[],
): string[] {
  return ids.filter(
    (id): id is string => typeof id === "string" && id.length > 0,
  );
}
