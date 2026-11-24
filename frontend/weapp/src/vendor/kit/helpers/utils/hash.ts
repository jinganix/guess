// DO NOT EDIT: GENERATED FROM kit@6e62ecb9756df2aad5829162f26ebf8840a392ea by tool/vendor (toolVersion 2). Host edits belong outside vendor/kit. See vendor-lock.json.
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
