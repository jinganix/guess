/*
 * Copyright (c) 2020 jinganix@gmail.com, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const _require = require;
const { pathsToModuleNameMapper } = _require("ts-jest");
const { compilerOptions } = _require("./tsconfig");

module.exports = {
  bail: true,
  collectCoverage: true,
  collectCoverageFrom: ["src/**/*.{js,ts}", "!src/**/*.d.ts"],
  coverageThreshold: {
    global: {
      branches: 0,
      functions: 0,
      lines: 0,
      statements: 0,
    },
  },
  errorOnDeprecated: true,
  moduleNameMapper: {
    ...pathsToModuleNameMapper(compilerOptions.paths, {
      prefix: "<rootDir>/",
    }),
  },
  modulePaths: [],
  preset: "ts-jest",
  resetMocks: true,
  rootDir: "./",
  roots: ["<rootDir>"],
  setupFiles: ["./__tests__/setup.wx.ts"],
  testMatch: ["<rootDir>/src/**/__tests__/**/*.{js,ts}", "<rootDir>/src/**/*.{spec,test}.{js,ts}"],
  watchPlugins: ["jest-watch-typeahead/filename", "jest-watch-typeahead/testname"],
};
