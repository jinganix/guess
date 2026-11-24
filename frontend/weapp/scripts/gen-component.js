// Scaffold a new component: 5 files that pass check:wxml + props-consistency
// out of the box. Usage: node scripts/gen-component.js foo-bar [--out dir]
const fs = require("fs");
const path = require("path");

const HEADER = `/*
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
 *
 * https://github.com/jinganix/guess
 */
`;

const toClass = (kebab) =>
  kebab
    .split("-")
    .map((p) => p[0].toUpperCase() + p.slice(1))
    .join("");

const files = (name) => {
  const cls = `${toClass(name)}Script`;
  return {
    "index.json": `{\n  "component": true,\n  "usingComponents": {}\n}\n`,
    "index.scss": "",
    "index.ts": `${HEADER}\nimport { ${cls} } from "@comps/${name}/script";\nimport { defaultComponent } from "@helpers/wx/adapter";\n\nComponent(defaultComponent(${cls}, (x) => new ${cls}(x), {}));\n`,
    "index.wxml": `<view wx:if="{{show}}"></view>\n`,
    "script.ts": `${HEADER}\nimport { classId } from "@helpers/utils/utils";\nimport { ScriptedComponent } from "@helpers/wx/adapter.types";\nimport { ComponentScript } from "@helpers/wx/component.script";\nimport { observable } from "mobx";\n\nexport class ${cls} extends ComponentScript {\n  static readonly CLASS_ID = classId();\n  @observable accessor show = false;\n\n  constructor(comp: ScriptedComponent) {\n    super(comp);\n  }\n\n  classId(): string {\n    return ${cls}.CLASS_ID;\n  }\n}\n`,
  };
};

const main = () => {
  const [name, ...rest] = process.argv.slice(2);
  if (!name || !/^[a-z][a-z0-9-]*$/.test(name)) {
    console.error("usage: node scripts/gen-component.js <kebab-case-name> [--out dir]");
    process.exitCode = 1;
    return;
  }
  const outFlag = rest.indexOf("--out");
  const base = outFlag === -1 ? path.join(__dirname, "..", "src", "components") : rest[outFlag + 1];
  const dir = path.join(base, name);
  if (fs.existsSync(dir)) {
    console.error(`${dir} already exists`);
    process.exitCode = 1;
    return;
  }
  fs.mkdirSync(dir, { recursive: true });
  for (const [file, content] of Object.entries(files(name))) {
    fs.writeFileSync(path.join(dir, file), content);
  }
  console.log(`created ${dir}`);
};

if (require.main === module) {
  main();
}

module.exports = { files };
