#!/usr/bin/env sh
# Enable repo-local git hooks (no Node/npm required).
set -e

ROOT=$(git rev-parse --show-toplevel)
cd "$ROOT"

# 本仓统一使用 shell hooks，拒绝第二套校验（husky / commitlint）。
if [ -d .husky ]; then
  echo "setup-git-hooks: refuse to enable — found .husky/ (use shell .githooks only)" >&2
  exit 1
fi
if ls commitlint.config.* 2>/dev/null | grep -q .; then
  echo "setup-git-hooks: refuse to enable — found commitlint config (use shell .githooks only)" >&2
  exit 1
fi
if git grep -l -e husky -e commitlint -- '*package.json' >/dev/null 2>&1; then
  echo "setup-git-hooks: refuse to enable — package.json references husky/commitlint (use shell .githooks only)" >&2
  git grep -n -e husky -e commitlint -- '*package.json' >&2 || true
  exit 1
fi

git config core.hooksPath .githooks
echo "Git hooks enabled: core.hooksPath=.githooks"
echo "  pre-commit:  block direct commits to master + docs/scripts/check.sh（编译/lint/单测）"
echo "  commit-msg:  docs/scripts/validate-commit-msg.sh"
echo "  pre-push:    block direct push to master + docs/scripts/check.sh"
