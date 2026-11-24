// DO NOT EDIT: GENERATED FROM kit@c32c22eb4ddd729c36ecc8ee2c28362a54bf417a by tool/vendor (toolVersion 1). Host edits belong outside vendor/kit. See vendor-lock.json.
import { Hash } from "./hash";

export class FakeRandom {
  private static readonly BAD_RANGE = "bound must be greater than origin";

  static nextInt(seed: number): number {
    return Hash.hash32(seed);
  }

  static nextIntInRange(seed: number, origin: number, bound: number): number {
    if (origin >= bound) {
      throw new Error(FakeRandom.BAD_RANGE);
    }
    const h = Hash.hash32(seed);
    const range = bound - origin;
    const mod = ((h % range) + range) % range;
    return origin + mod;
  }

  static nextBoolean(seed: number): boolean {
    return Hash.hash32(seed) % 2 === 0;
  }

  static randomOne<T>(seed: number, values: T[]): T {
    if (values.length === 0) {
      throw new Error("Random one from an empty list.");
    }
    if (values.length === 1) {
      return values[0];
    }
    const idx = FakeRandom.nextIntInRange(seed, 0, values.length);
    return values[idx];
  }
}
