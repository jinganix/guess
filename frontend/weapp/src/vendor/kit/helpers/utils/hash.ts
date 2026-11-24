// DO NOT EDIT: GENERATED FROM kit@c32c22eb4ddd729c36ecc8ee2c28362a54bf417a by tool/vendor (toolVersion 1). Host edits belong outside vendor/kit. See vendor-lock.json.
export class Hash {
  /** Deterministic string hash; feeds into {@link hash32} for FakeRandom seeds. */
  static hashStr(s: string): number {
    let x = 0;
    for (let i = 0; i < s.length; i++) {
      x = (x * 31 + s.charCodeAt(i)) | 0;
    }
    return Hash.hash32(x);
  }

  static hash32(x: number): number {
    x = x | 0;
    x = (((x >>> 16) ^ x) * 0x45d9f3b) | 0;
    x = (((x >>> 16) ^ x) * 0x45d9f3b) | 0;
    x = (x >>> 16) ^ x;
    return x >= 0 ? x : -x;
  }
}
