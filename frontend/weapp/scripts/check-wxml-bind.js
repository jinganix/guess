// WXML 绑定校验：index.wxml 里的 {{x}} 首段必须能在同目录 script.ts
// （@observable accessor / getter）、index.ts（properties）、wxs 模块名、
// wx:for 循环变量中找到，否则报错。字段提取复用 pick-loader 的 extractPicks。
const fs = require("fs");
const path = require("path");
const { extractPicks } = require("../webpack/pick-loader/extract");

const scriptFields = (src) => {
  const fields = new Set();
  for (const m of src.matchAll(/@observable\s+accessor\s+(\w+)/g)) {
    fields.add(m[1]);
  }
  for (const m of src.matchAll(/^\s*accessor\s+(\w+)/gm)) {
    fields.add(m[1]);
  }
  for (const m of src.matchAll(/get\s+(\w+)\s*\(/g)) {
    fields.add(m[1]);
  }
  return fields;
};

const propsFields = (src) => {
  const fields = new Set();
  for (const m of src.matchAll(/(\w+)\s*:\s*\{\s*type\s*:/g)) {
    fields.add(m[1]);
  }
  return fields;
};

const wxsModules = (wxml) => {
  const modules = new Set();
  for (const m of wxml.matchAll(/<wxs[^>]*module=["']([\w-]+)["']/g)) {
    modules.add(m[1]);
  }
  return modules;
};

// wx:for 默认循环变量 item/index；自定义 wx:for-item="x" 一并收录。
// shortcut: 按文件级白名单而非逐元素作用域，同一文件内误判率极低。
const loopLocals = (wxml) => {
  const locals = new Set();
  if (/wx:for\s*=/.test(wxml)) {
    locals.add("item").add("index");
  }
  for (const m of wxml.matchAll(/wx:for-item\s*=\s*"{{?(\w+)}}?"/g)) {
    locals.add(m[1]);
  }
  for (const m of wxml.matchAll(/wx:for-index\s*=\s*"{{?(\w+)}}?"/g)) {
    locals.add(m[1]);
  }
  return locals;
};

// 遗留写法：index.ts 里 Component({data: {...}}) 内联数据（如 preview-page）。
// 用括号计数取 data 块顶层 key。
const dataFields = (src) => {
  const fields = new Set();
  const m = src.match(/data\s*:\s*\{/);
  if (!m) {
    return fields;
  }
  let depth = 0;
  let block = "";
  for (const ch of src.slice(m.index + m[0].length - 1)) {
    if (ch === "{") {
      depth++;
    } else if (ch === "}") {
      depth--;
      if (depth === 0) {
        break;
      }
    }
    if (depth === 1 && ch !== "{") {
      block += ch;
    }
  }
  for (const k of block.matchAll(/"?(\w+)"?\s*:/g)) {
    fields.add(k[1]);
  }
  return fields;
};

const checkWxml = (wxml, fields) => {
  const errors = [];
  const wxs = wxsModules(wxml);
  const locals = loopLocals(wxml);
  for (const pick of extractPicks(wxml)) {
    const head = pick.split(".")[0];
    if (/^\d+$/.test(head)) {
      continue;
    }
    if (wxs.has(head) || locals.has(head) || fields.has(head)) {
      continue;
    }
    if (!errors.includes(head)) {
      errors.push(head);
    }
  }
  return errors;
};

const ALIAS_ROOTS = {
  "@comps": "src/components",
  "@helpers": "src/helpers",
  "@modules": "src/modules",
  "@pages": "src/pages",
  "@proto": "generated/proto",
};

// index.ts 可能复用别处目录的 script（如 user/moments 用 moment/list 的
// MomentListScript）：把指向 */script 的 import 解析成绝对路径一并校验。
const resolveScriptImports = (indexSrc, dir, root) => {
  const out = [];
  for (const m of indexSrc.matchAll(/from\s+["']([^"']+)\/script["']/g)) {
    const spec = m[1];
    let abs = null;
    const alias = Object.keys(ALIAS_ROOTS).find((a) => spec === a || spec.startsWith(`${a}/`));
    if (alias) {
      abs = path.join(root, ALIAS_ROOTS[alias], spec.slice(alias.length), "script.ts");
    } else if (spec.startsWith(".")) {
      abs = path.join(dir, `${spec}/script.ts`);
    }
    if (abs && fs.existsSync(abs)) {
      out.push(abs);
    }
  }
  return out;
};

const checkFile = (dir) => {
  const wxml = fs.readFileSync(path.join(dir, "index.wxml"), "utf-8");
  const fields = new Set();
  const addFields = (file) => {
    for (const f of scriptFields(fs.readFileSync(file, "utf-8"))) {
      fields.add(f);
    }
  };
  const scriptPath = path.join(dir, "script.ts");
  if (fs.existsSync(scriptPath)) {
    addFields(scriptPath);
  }
  const indexPath = path.join(dir, "index.ts");
  let indexSrc = "";
  if (fs.existsSync(indexPath)) {
    indexSrc = fs.readFileSync(indexPath, "utf-8");
    for (const f of propsFields(indexSrc)) {
      fields.add(f);
    }
    for (const f of dataFields(indexSrc)) {
      fields.add(f);
    }
  }
  const root = path.join(__dirname, "..");
  for (const imp of resolveScriptImports(indexSrc, dir, root)) {
    addFields(imp);
  }
  return checkWxml(wxml, fields);
};

const collectDirs = (root) => {
  const out = [];
  const walk = (dir) => {
    for (const name of fs.readdirSync(dir, { withFileTypes: true })) {
      if (name.isDirectory()) {
        walk(path.join(dir, name.name));
      } else if (name.name === "index.wxml") {
        out.push(dir);
      }
    }
  };
  walk(root);
  return out;
};

const main = () => {
  const roots = process.argv.slice(2).length
    ? process.argv.slice(2)
    : [path.join(__dirname, "..", "src", "components"), path.join(__dirname, "..", "src", "pages")];
  let failed = false;
  for (const root of roots) {
    for (const dir of collectDirs(root)) {
      const errors = checkFile(dir);
      if (errors.length) {
        failed = true;
        console.error(`${path.relative(process.cwd(), dir)}/index.wxml: unknown bindings: ${errors.join(", ")}`);
      }
    }
  }
  if (failed) {
    process.exitCode = 1;
  }
};

module.exports = { checkFile, checkWxml, collectDirs, dataFields, loopLocals, propsFields, resolveScriptImports, scriptFields };

if (require.main === module) {
  main();
}
