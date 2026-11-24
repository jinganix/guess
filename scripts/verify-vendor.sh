#!/usr/bin/env bash
#
# verify-vendor.sh -- in-repo regression net for the buyout pilot (finding 9).
#
# Checks, per target (frontend/weapp/src + service/.../guess):
#   H1 every lock-listed file starts with the tool-generated header
#      (`DO NOT EDIT` + `GENERATED FROM kit@<lock kitSha>`);
#   H2 `.vendor/base/<dest>` exists and is byte-identical to `<dest>`;
#   H3 lock `baseHash`/`destHash` equal sha256 of the file bytes
#      (hence baseHash == destHash);
#   R1 no kit-residue references in either vendor tree
#      (`io.github.jinganix.kit|jinganix/kit|@kit/|code-kit/`);
#   R2 no weapp-forbidden runtime globals in the TS vendor tree
#      (`document|window|localStorage|sessionStorage|navigator|HTMLElement|fetch(`).
#
# Usage: bash scripts/verify-vendor.sh   (from the repo root; also wired as
# `npm run verify:vendor` in frontend/weapp). Exit 0 on PASS, 1 on FAIL.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TARGETS=(
  "frontend/weapp/src"
  "service/src/main/java/io/github/jinganix/guess"
)

command -v jq >/dev/null 2>&1 || { echo "verify-vendor: FAIL: jq is required" >&2; exit 1; }

hash_file() {
  if command -v sha256sum >/dev/null 2>&1; then sha256sum "$1" | cut -d' ' -f1
  else shasum -a 256 "$1" | cut -d' ' -f1; fi
}

FAIL=0
fail() { echo "verify-vendor: FAIL: $1" >&2; FAIL=$((FAIL + 1)); }

for target in "${TARGETS[@]}"; do
  root="$ROOT/$target"
  lock="$root/vendor-lock.json"
  base="$root/.vendor/base"
  [ -f "$lock" ] || { fail "missing $target/vendor-lock.json"; continue; }
  kit_sha="$(jq -r '.kitSha' "$lock")"
  while IFS= read -r dest; do
    [ -n "$dest" ] || continue
    file="$root/$dest"
    base_file="$base/$dest"
    [ -f "$file" ] || { fail "$target/$dest: dest file missing"; continue; }
    [ -f "$base_file" ] || { fail "$target/$dest: base snapshot missing"; continue; }
    head -n 1 "$file" | grep -q '^// DO NOT EDIT: GENERATED FROM kit@' \
      || fail "$target/$dest: tool header missing on line 1"
    head -n 1 "$file" | grep -q "GENERATED FROM kit@${kit_sha}" \
      || fail "$target/$dest: header kitSha != lock kitSha ($kit_sha)"
    cmp -s "$file" "$base_file" \
      || fail "$target/$dest: dest differs from .vendor/base copy"
    want="$(hash_file "$file")"
    got_base="$(jq -r --arg d "$dest" '.files[] | select(.destPath == $d) | .baseHash' "$lock")"
    got_dest="$(jq -r --arg d "$dest" '.files[] | select(.destPath == $d) | .destHash' "$lock")"
    [ "$want" = "$got_base" ] && [ "$want" = "$got_dest" ] \
      || fail "$target/$dest: lock hash != file sha256 ($want)"
  done < <(jq -r '.files[].destPath' "$lock")

  if grep -rE "io\.github\.jinganix\.kit|jinganix/kit|@kit/|code-kit/" "$root/vendor" 2>/dev/null; then
    fail "$target/vendor: kit-residue references found (see above)"
  fi
done

if grep -rEw "document|window|localStorage|sessionStorage|navigator|HTMLElement" \
  "$ROOT/frontend/weapp/src/vendor" 2>/dev/null; then
  fail "frontend vendor tree references weapp-forbidden globals (see above)"
fi
if grep -rE "(^|[^a-zA-Z_])fetch\(" "$ROOT/frontend/weapp/src/vendor" 2>/dev/null; then
  fail "frontend vendor tree references fetch( (see above)"
fi

if [ "$FAIL" -ne 0 ]; then
  echo "verify-vendor: FAIL ($FAIL checks)" >&2
  exit 1
fi
echo "verify-vendor: PASS (headers + base==dest + lock hashes + residue + weapp globals)"
