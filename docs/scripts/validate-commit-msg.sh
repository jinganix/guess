#!/usr/bin/env sh
# Validates the first line of a git commit message (Conventional Commits).
# Rules mirror CONTRIBUTING.md scope table.

set -e

COMMIT_MSG_FILE="${1:?usage: validate-commit-msg.sh <commit-msg-file>}"
# Read once: two `sed` passes over the same path drain a piped argument (e.g. /dev/stdin),
# which would make the later checks pass vacuously.
CONTENT=$(cat "$COMMIT_MSG_FILE")
HEADER=$(printf '%s\n' "$CONTENT" | sed -n '1p')

# Merge commits
case "$HEADER" in
  Merge*) exit 0
esac

TYPES='feat|fix|docs|style|refactor|perf|test|build|ci|chore|revert'
SCOPES='service|frontend|proto|build|ci|docs|deps'
PATTERN="^(${TYPES})\\((${SCOPES})\\): .+"

if ! printf '%s\n' "$HEADER" | grep -qE "$PATTERN"; then
  echo "commit-msg: invalid header — expected: <type>(<scope>): <subject>"
  echo "  types:  feat fix docs style refactor perf test build ci chore revert"
  echo "  scopes: service frontend proto build ci docs deps"
  echo "  see CONTRIBUTING.md"
  exit 1
fi

if [ "${#HEADER}" -gt 100 ]; then
  echo "commit-msg: header exceeds 100 characters (${#HEADER})"
  exit 1
fi

# English-only: commit message must be ASCII (no CJK / accented / emoji).
# LC_ALL=C makes [:print:] / [:space:] ASCII-only, so any non-ASCII byte matches.
# Only the message counts. `git commit -v` appends git's template comments and the staged
# diff after a scissors line (cut marker matched without hard-coding the comment character),
# and git strips those after the hook runs — so cut there and let
# `git stripspace --strip-comments` drop the template comments exactly as git does.
# Without a scissors line git keeps every line the author wrote (`-m`/`-F` use the
# `whitespace` cleanup), so the whole file is the message and nothing may be dropped.
SCISSORS='^[^[:alnum:][:space:]] ------------------------ >8 ------------------------'
if printf '%s\n' "$CONTENT" | grep -qE "$SCISSORS"; then
  MSG=$(printf '%s\n' "$CONTENT" | sed -e "/$SCISSORS/,\$d" | git stripspace --strip-comments)
else
  MSG=$CONTENT
fi
if printf '%s\n' "$MSG" | LC_ALL=C grep -q '[^[:print:][:space:]]'; then
  echo "commit-msg: non-ASCII characters found — use English only (ASCII)"
  echo "  see CONTRIBUTING.md"
  exit 1
fi

exit 0
