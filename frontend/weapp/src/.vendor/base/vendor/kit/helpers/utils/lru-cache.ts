// DO NOT EDIT: GENERATED FROM kit@c32c22eb4ddd729c36ecc8ee2c28362a54bf417a by tool/vendor (toolVersion 1). Host edits belong outside vendor/kit. See vendor-lock.json.
/**
 * Minimal browser-safe LRU cache with per-entry TTL.
 *
 * Replaces the `lru-cache` npm package in browser bundles: `lru-cache@11`
 * imports `node:diagnostics_channel`, which Vite externalizes for the browser
 * and logs `Module "node:diagnostics_channel" has been externalized...`
 * warnings at startup. This implementation uses only `Map` + `Date.now()`,
 * so it bundles cleanly for web / Capacitor.
 *
 * Supported surface is intentionally limited to what the hosts use:
 * `get` / `set` / `getRemainingTTL`, plus `max` (entry count) and `ttl`
 * (milliseconds) constructor options. Extra `lru-cache` options (`maxSize`,
 * `sizeCalculation`, `allowStale`, `updateAgeOnGet`, `updateAgeOnHas`) are
 * accepted and ignored: entries never return stale values and `get` always
 * refreshes recency for eviction (but never extends TTL).
 */
export type SimpleLRUCacheOptions = {
  max: number;
  ttl: number;
  maxSize?: number;
  sizeCalculation?: (value: unknown, key: string) => number;
  allowStale?: boolean;
  updateAgeOnGet?: boolean;
  updateAgeOnHas?: boolean;
};

export class SimpleLRUCache<K, V> {
  private readonly max: number;
  private readonly ttl: number;
  private readonly values = new Map<K, V>();
  private readonly expiresAt = new Map<K, number>();

  constructor(options: SimpleLRUCacheOptions) {
    this.max = options.max > 0 ? Math.floor(options.max) : 500;
    this.ttl = options.ttl > 0 ? options.ttl : 0;
  }

  get(key: K): V | undefined {
    if (!this.values.has(key)) {
      return undefined;
    }
    if (this.isExpired(key, Date.now())) {
      this.values.delete(key);
      this.expiresAt.delete(key);
      return undefined;
    }
    // Refresh LRU recency without extending TTL.
    const value = this.values.get(key) as V;
    this.values.delete(key);
    this.values.set(key, value);
    return value;
  }

  set(key: K, value: V): void {
    const now = Date.now();
    if (this.values.has(key)) {
      this.values.delete(key);
      this.expiresAt.delete(key);
    }
    this.values.set(key, value);
    this.expiresAt.set(
      key,
      this.ttl > 0 ? now + this.ttl : Number.POSITIVE_INFINITY,
    );
    this.evictExpired(now);
    while (this.values.size > this.max) {
      const oldest = this.values.keys().next();
      if (oldest.done) {
        break;
      }
      this.values.delete(oldest.value);
      this.expiresAt.delete(oldest.value);
    }
  }

  /**
   * Remaining TTL in milliseconds. Returns `0` when the key is missing or
   * expired (mirrors `lru-cache` semantics relied upon by the player/user
   * caches: missing entries must read as "needs load").
   */
  getRemainingTTL(key: K): number {
    if (!this.values.has(key)) {
      return 0;
    }
    const expires = this.expiresAt.get(key);
    if (expires === undefined) {
      return 0;
    }
    if (!Number.isFinite(expires)) {
      return Number.POSITIVE_INFINITY;
    }
    const remaining = expires - Date.now();
    if (remaining <= 0) {
      this.values.delete(key);
      this.expiresAt.delete(key);
      return 0;
    }
    return remaining;
  }

  private isExpired(key: K, now: number): boolean {
    const expires = this.expiresAt.get(key);
    if (expires === undefined) {
      return true;
    }
    if (!Number.isFinite(expires)) {
      return false;
    }
    return expires <= now;
  }

  private evictExpired(now: number): void {
    for (const [key, expires] of this.expiresAt) {
      if (Number.isFinite(expires) && (expires as number) <= now) {
        this.values.delete(key);
        this.expiresAt.delete(key);
      }
    }
  }
}
