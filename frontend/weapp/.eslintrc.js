module.exports = {
  env: {
    es6: true,
    jest: true,
    node: true,
  },
  extends: ["plugin:@typescript-eslint/recommended", "plugin:prettier/recommended"],
  overrides: [
    {
      files: ["*.ts", "*.tsx"],
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
            argsIgnorePattern: "_",
          },
        ],
        "@typescript-eslint/quotes": [
          "error",
          "double",
          {
            allowTemplateLiterals: true,
          },
        ],
      },
    },
  ],
  parser: "@typescript-eslint/parser",
  plugins: ["import", "sort-keys-fix"],
  root: true,
  rules: {
    "@typescript-eslint/no-unused-vars": [
      "error",
      {
        argsIgnorePattern: "_",
      },
    ],
    "import/newline-after-import": "error",
    "import/order": [
      "error",
      {
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
    "require-jsdoc": [
      "error",
      {
        require: {
          ArrowFunctionExpression: false,
          ClassDeclaration: false,
          FunctionDeclaration: false,
          FunctionExpression: false,
          MethodDefinition: false,
        },
      },
    ],
    semi: "off",
    "sort-keys": ["error"],
    "sort-keys-fix/sort-keys-fix": "warn",
  },
};
