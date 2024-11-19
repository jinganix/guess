import { fixupPluginRules } from "@eslint/compat";
import eslint from "@eslint/js";
import tsParser from "@typescript-eslint/parser";
import eslintPluginImport from "eslint-plugin-import";
import eslintPluginPrettierRecommended from "eslint-plugin-prettier/recommended";
import sortKeysFix from "eslint-plugin-sort-keys-fix";
import globals from "globals";
import tseslint from "typescript-eslint";

export default [
  eslint.configs.recommended,
  ...tseslint.configs.recommended,
  {
    languageOptions: {
      globals: {
        ...globals.jest,
        ...globals.node,
      },
      parser: tsParser,
    },
    plugins: {
      import: fixupPluginRules(eslintPluginImport),
      "sort-keys-fix": sortKeysFix,
    },
    rules: {
      "@typescript-eslint/no-empty-object-type": ["off"],
      "@typescript-eslint/no-unused-expressions": [
        "error",
        { allowShortCircuit: true, allowTernary: true },
      ],
      "@typescript-eslint/no-unused-vars": [
        "error",
        {
          argsIgnorePattern: "^_",
          caughtErrors: "none",
          varsIgnorePattern: "^_",
        },
      ],
      "import/newline-after-import": "error",
      "import/order": [
        "error",
        {
          alphabetize: {
            caseInsensitive: true,
            order: "asc",
          },
          groups: [["builtin", "external"], "internal", ["index", "parent", "sibling"]],
        },
      ],
      "max-len": [
        "error",
        {
          code: 100,
          ignoreStrings: true,
          ignoreUrls: true,
        },
      ],
      semi: "off",
      "sort-keys": [
        "error",
        "asc",
        {
          caseSensitive: false,
          natural: true,
        },
      ],
      "sort-keys-fix/sort-keys-fix": [
        "warn",
        "asc",
        {
          caseSensitive: false,
          natural: true,
        },
      ],
    },
  },
  {
    files: ["**/*.ts", "**/*.tsx"],
    rules: {
      "@typescript-eslint/explicit-function-return-type": [
        "error",
        {
          allowConciseArrowFunctionExpressionsStartingWithVoid: true,
          allowDirectConstAssertionInArrowFunctions: true,
          allowExpressions: true,
          allowHigherOrderFunctions: true,
          allowTypedFunctionExpressions: true,
        },
      ],
      "@typescript-eslint/no-unused-vars": [
        "error",
        {
          argsIgnorePattern: "^_",
          caughtErrors: "none",
          varsIgnorePattern: "^_",
        },
      ],
    },
  },
  eslintPluginPrettierRecommended,
];
