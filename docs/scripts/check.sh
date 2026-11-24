#!/usr/bin/env bash
# 提交/推送前门禁：后端编译 + 格式检查 + 前端 lint/单测。
# 由 .githooks/pre-commit 与 .githooks/pre-push 调用；提交前也可手动跑。
# 紧急绕行：SKIP_CHECKS=1 ./docs/scripts/check.sh（仅限确有理由时）。
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
cd "$ROOT"

if [ "${SKIP_CHECKS:-0}" = "1" ]; then
  # Bypass must never look like a green gate: loud on stderr, and a hard fail under CI.
  echo "check: SKIP_CHECKS=1 — 跳过编译 / lint / 测试 (bypass, NOT a pass)" >&2
  if [ "${CI:-}" = "true" ]; then
    echo "check: SKIP_CHECKS=1 is forbidden under CI" >&2
    exit 1
  fi
  exit 0
fi

echo "== [1/2] 后端：编译 + 格式检查 =="
./gradlew :service:guess:compileTestJava spotlessCheck

echo "== [2/2] 前端：lint + 单测 =="
if [ ! -d frontend/weapp/node_modules ]; then
  echo "-- npm ci frontend/weapp"
  npm --prefix frontend/weapp ci --no-audit --no-fund
fi
npm --prefix frontend/weapp run lint
npm --prefix frontend/weapp test

echo "check: OK — 编译 / 格式 / lint / 单测全部通过"
