#!/usr/bin/env bash
#
# vendor-apply-headers.sh -- deterministic post-vendor step for the buyout pilot.
#
# Tool follow-up (code-kit): fold this into tool/vendor/lib/rewrite.sh and bump
# TOOL_VERSION to 2, so headers + QuerySource ship from `vendor.sh` directly.
# Until then every `vendor.sh` re-run MUST be followed by this script
# (vendor.sh rewrites the files and drops the headers).
#
# What it does (idempotent; never hand-edit vendor/kit files, run this instead):
#   1. Prepend a one-line `// DO NOT EDIT: GENERATED FROM kit@<sha> ...` header to
#      every file listed in each per-target vendor-lock.json (frontend + service),
#      mirroring the result into `.vendor/base/` so baseHash == destHash holds.
#   2. Apply the QuerySource host-patch to the frontend
#      `vendor/kit/helpers/utils/query-params.ts` (+ base mirror): narrow
#      `fromQuery(t, source?: URLSearchParams)` to a minimal `{ has, get }`
#      surface so the weapp adapter no longer needs `as URLSearchParams`.
#      A real URLSearchParams still satisfies QuerySource structurally.
#      Upstream issue: adopt QuerySource in the kit source.
#   3. Recompute baseHash/destHash for every lock entry from the final bytes.
#
# Usage: bash scripts/vendor-apply-headers.sh   (from the repo root)
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TARGETS=(
  "frontend/weapp/src"
  "service/guess/src/main/java/io/github/jinganix/guess"
)

command -v jq >/dev/null 2>&1 || { echo "vendor-apply-headers: jq is required" >&2; exit 2; }
command -v python3 >/dev/null 2>&1 || { echo "vendor-apply-headers: python3 is required" >&2; exit 2; }

hash_file() { # <file>: sha256 of file bytes
  if command -v sha256sum >/dev/null 2>&1; then sha256sum "$1" | cut -d' ' -f1
  else shasum -a 256 "$1" | cut -d' ' -f1; fi
}

header_for() { # <lock>: header line for this target's lock
  local kit_sha tool_version
  kit_sha="$(jq -r '.kitSha' "$1")"
  tool_version="$(jq -r '.toolVersion' "$1")"
  printf '// DO NOT EDIT: GENERATED FROM kit@%s by tool/vendor (toolVersion %s). Host edits belong outside vendor/kit. See vendor-lock.json.' \
    "$kit_sha" "$tool_version"
}

apply_headers_to_target() { # <targetRoot>
  local root="$1" lock="$1/vendor-lock.json"
  [ -f "$lock" ] || { echo "vendor-apply-headers: missing $lock" >&2; exit 1; }
  local header
  header="$(header_for "$lock")"
  local dest
  while IFS= read -r dest; do
    [ -n "$dest" ] || continue
    local file="$root/$dest"
    [ -f "$file" ] || { echo "vendor-apply-headers: missing $file" >&2; exit 1; }
    # Strip any previous header line, then prepend the fresh one (idempotent).
    local tmp
    tmp="$(mktemp)"
    grep -v '^// DO NOT EDIT: GENERATED FROM kit@' "$file" >"$tmp" || true
    { printf '%s\n' "$header"; cat "$tmp"; } >"$file"
    rm -f "$tmp"
  done < <(jq -r '.files[].destPath' "$lock")
}

apply_querysource_patch() { # <weappSrcRoot>: narrow fromQuery to QuerySource
  local file="$1/vendor/kit/helpers/utils/query-params.ts"
  [ -f "$file" ] || { echo "vendor-apply-headers: missing $file" >&2; exit 1; }
  python3 - "$file" <<'EOF'
import sys
p = sys.argv[1]
s = open(p, encoding='utf-8').read()
old = "/** Overlay URL query params onto a defaults object (numbers/booleans coerced). */\nexport function fromQuery<T>(t: T, source?: URLSearchParams): T {"
new = """/** Minimal query-source read surface for fromQuery (host patch: `URLSearchParams`
 * narrowed to `{ has, get }` so weapp `onLoad` records adapt without a cast; a real
 * `URLSearchParams` still satisfies this structurally). Upstream: adopt in kit source. */
export interface QuerySource {
  has(key: string): boolean;
  get(key: string): string | null;
}

/** Overlay URL query params onto a defaults object (numbers/booleans coerced). */
export function fromQuery<T>(t: T, source?: QuerySource): T {"""
if 'interface QuerySource' in s:
    s = s.replace(
        'fromQuery<T>(t: T, source?: URLSearchParams)',
        'fromQuery<T>(t: T, source?: QuerySource)')
else:
    assert old in s, 'query-params.ts anchor not found in %s' % p
    s = s.replace(old, new)
open(p, 'w', encoding='utf-8').write(s)
EOF
}

mirror_and_refresh_lock() { # <targetRoot>: base := dest, lock hashes := sha256(dest)
  local root="$1" lock="$1/vendor-lock.json" base="$1/.vendor/base"
  local entries
  entries="$(mktemp)"
  local dest
  while IFS= read -r dest; do
    [ -n "$dest" ] || continue
    mkdir -p "$base/$(dirname "$dest")"
    cp "$root/$dest" "$base/$dest"
    printf '%s\t%s\n' "$dest" "$(hash_file "$root/$dest")" >>"$entries"
  done < <(jq -r '.files[].destPath' "$lock")
  local map_json lock_tmp
  map_json="$(jq -R -s 'split("\n") | map(select(length > 0) | split("\t") | {(.[0]): .[1]}) | add' "$entries")"
  rm -f "$entries"
  lock_tmp="$(mktemp)"
  jq --argjson H "$map_json" \
    '.files |= map(.baseHash = $H[.destPath] | .destHash = $H[.destPath])' \
    "$lock" >"$lock_tmp"
  mv -f "$lock_tmp" "$lock"
}

for target in "${TARGETS[@]}"; do
  apply_headers_to_target "$ROOT/$target"
done
apply_querysource_patch "$ROOT/frontend/weapp/src"
for target in "${TARGETS[@]}"; do
  mirror_and_refresh_lock "$ROOT/$target"
done

echo "vendor-apply-headers: headers + QuerySource applied, locks refreshed" >&2
