// Build-time helper: derive template binding paths from a wxml file.
// Used by ./index.js (pick-loader). Pure functions, covered by extract.test.js.
const acorn = require("acorn");
const walk = require("acorn-walk");

// Identifiers that look like bindings but are JS globals.
const JS_GLOBALS = new Set(["undefined", "NaN", "Infinity"]);

// Ancestors passed by acorn-walk include the node itself as last element.
function containsType(ancestors, type) {
  return ancestors.slice(0, -1).some((x) => x.type === type);
}

function extractWxsModules(wxml) {
  const modules = new Set();
  const re = /<wxs[^>]*module=["']([\w-]+)["']/g;
  let match;
  while ((match = re.exec(wxml)) !== null) {
    modules.add(match[1]);
  }
  return modules;
}

// Dotted path of a member expression, ignoring computed keys
// (`options[item]` -> `options`, `options['a']` -> `options.a`).
function memberPath(node) {
  if (!node) {
    return null;
  }
  if (node.type === "Identifier") {
    return node.name;
  }
  if (node.type !== "MemberExpression") {
    return null;
  }
  const object = memberPath(node.object);
  if (object === null) {
    return null;
  }
  if (node.computed) {
    if (node.property.type === "Literal" && typeof node.property.value === "string") {
      return `${object}.${node.property.value}`;
    }
    return object;
  }
  if (node.object.computed) {
    // Unresolvable at build time (`options[item].length`): keep the base only.
    return object;
  }
  return `${object}.${node.property.name}`;
}

// Extra bindings contributed by a computed key: `item` in `options[item]`.
function keyPaths(property, variables) {
  if (property.type === "Identifier") {
    variables.add(property.name);
    return;
  }
  if (property.type === "MemberExpression") {
    const path = memberPath(property);
    if (path !== null) {
      variables.add(path);
    }
    return;
  }
  if (property.type === "Literal") {
    return;
  }
  walk.simple(property, {
    Identifier(node) {
      if (!JS_GLOBALS.has(node.name)) {
        variables.add(node.name);
      }
    },
  });
}

function extractAccessExpressions(code, variables) {
  const ast = acorn.parse(code, { ecmaVersion: "latest" });
  walk.ancestor(ast, {
    Identifier(node, ancestors) {
      if (containsType(ancestors, "MemberExpression")) {
        return;
      }
      for (const ancestor of ancestors) {
        if (ancestor.type === "CallExpression" && ancestor.callee?.name === node.name) {
          return;
        }
      }
      if (!JS_GLOBALS.has(node.name)) {
        variables.add(node.name);
      }
    },
    MemberExpression(node, ancestors) {
      if (containsType(ancestors, "MemberExpression")) {
        // Still collect computed keys buried in a chain (`options[item].length`).
        if (node.computed && node.property.type !== "Literal") {
          keyPaths(node.property, variables);
        }
        return;
      }
      const path = memberPath(node);
      if (path !== null) {
        variables.add(path);
      }
      if (node.computed && node.property.type !== "Literal") {
        keyPaths(node.property, variables);
      }
    },
  });
}

function extractPicks(wxml) {
  const wxsModules = extractWxsModules(wxml);
  const content = wxml.replace(/<!--[\s\S]*?-->/g, "");
  const regex = /{{([\s\S]*?)}}/g;
  const picks = new Set();
  let match;
  while ((match = regex.exec(content)) !== null) {
    const code = match[1].trim();
    if (!code) {
      continue;
    }
    const variables = new Set();
    try {
      extractAccessExpressions(code, variables);
    } catch (error) {
      throw new Error(`failed to parse binding expression ${JSON.stringify(code)}: ${error.message}`);
    }
    for (const variable of variables) {
      // `wx:for` loop variables (e.g. `item`) are intentionally kept:
      // they often name real script observables (see icon-follow),
      // and unknown names are harmless (ignored at runtime).
      if (!wxsModules.has(variable.split(".")[0])) {
        picks.add(variable);
      }
    }
  }
  return Array.from(picks).sort();
}

module.exports = { extractPicks };
