# Contributing

## Conventional Commits

Check if your commit messages meet the [conventional commit format](https://conventionalcommits.org).

The conventional config extends from [config-conventional](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional).

## Create a commit

Run `npm install` in root directory, then you will get [Commitizen](https://github.com/commitizen-tools/commitizen) installed.

Use `npm run cz` or `npx cz` to create a commit.

## Workflow validation

Commit message will be validated by workflow. If the validation is fail, amend the commit and rerun validation action.
