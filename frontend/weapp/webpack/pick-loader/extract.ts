// Build-time helper: derive template binding paths from a wxml file.
// Used by ./index (pick-loader). Pure functions, covered by extract.test.ts.
import * as acorn from "acorn";

// acorn-walk ships no types; loaded dynamically (cf. requireFile in ../utils).
// eslint-disable-next-line @typescript-eslint/no-var-requires
const walk = require("acorn-walk") as {
  ancestor(
    node: acorn.Node,
    visitors: Record<string, (node: acorn.Node, ancestors: acorn.Node[]) => void>,
  ): void;
  simple(node: acorn.Node, visitors: Record<string, (node: acorn.Node) => void>): void;
};

// Identifiers that look like bindings but are JS globals.
const JS_GLOBALS = new Set(["undefined", "NaN", "Infinity"]);

// Ancestors passed by acorn-walk include the node itself as last element.
function containsType(ancestors: acorn.Node[], type: string): boolean {
  return ancestors.slice(0, -1).some((x) => x.type === type);
}

function extractWxsModules(wxml: string): Set<string> {
  const modules = new Set<string>();
  const re = /<wxs[^>]*module=["']([\w-]+)["']/g;
  let match: RegExpExecArray | null;
  while ((match = re.exec(wxml)) !== null) {
    modules.add(match[1]);
  }
  return modules;
}

// Dotted path of a member expression, ignoring computed keys
// (`options[item]` -> `options`, `options['a']` -> `options.a`).
function memberPath(node: acorn.Node | null | undefined): string | null {
  if (!node) {
    return null;
  }
  if (node.type === "Identifier") {
    return (node as acorn.Identifier).name;
  }
  if (node.type !== "MemberExpression") {
    return null;
  }
  const member = node as acorn.MemberExpression;
  const object = memberPath(member.object as acorn.Node);
  if (object === null) {
    return null;
  }
  if (member.computed) {
    const property = member.property as acorn.Node;
    if (property.type === "Literal" && typeof (property as acorn.Literal).value === "string") {
      return `${object}.${(property as acorn.Literal).value}`;
    }
    return object;
  }
  if ((member.object as acorn.MemberExpression).computed) {
    // Unresolvable at build time (`options[item].length`): keep the base only.
    return object;
  }
  return `${object}.${(member.property as acorn.Identifier).name}`;
}

// Extra bindings contributed by a computed key: `item` in `options[item]`.
function keyPaths(property: acorn.Node, variables: Set<string>): void {
  if (property.type === "Identifier") {
    variables.add((property as acorn.Identifier).name);
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
    Identifier(node: acorn.Node) {
      const name = (node as acorn.Identifier).name;
      if (!JS_GLOBALS.has(name)) {
        variables.add(name);
      }
    },
  });
}

function extractAccessExpressions(code: string, variables: Set<string>): void {
  const ast = acorn.parse(code, { ecmaVersion: "latest" });
  walk.ancestor(ast, {
    Identifier(node: acorn.Node, ancestors: acorn.Node[]) {
      if (containsType(ancestors, "MemberExpression")) {
        return;
      }
      const name = (node as acorn.Identifier).name;
      for (const ancestor of ancestors) {
        const callee = (ancestor as acorn.CallExpression).callee as acorn.Identifier;
        if (ancestor.type === "CallExpression" && callee?.name === name) {
          return;
        }
      }
      if (!JS_GLOBALS.has(name)) {
        variables.add(name);
      }
    },
    MemberExpression(node: acorn.Node, ancestors: acorn.Node[]) {
      const member = node as acorn.MemberExpression;
      if (containsType(ancestors, "MemberExpression")) {
        // Still collect computed keys buried in a chain (`options[item].length`).
        if (member.computed && member.property.type !== "Literal") {
          keyPaths(member.property as acorn.Node, variables);
        }
        return;
      }
      const path = memberPath(node);
      if (path !== null) {
        variables.add(path);
      }
      if (member.computed && member.property.type !== "Literal") {
        keyPaths(member.property as acorn.Node, variables);
      }
    },
  });
}

export function extractPicks(wxml: string): string[] {
  const wxsModules = extractWxsModules(wxml);
  const content = wxml.replace(/<!--[\s\S]*?-->/g, "");
  const regex = /{{([\s\S]*?)}}/g;
  const picks = new Set<string>();
  let match: RegExpExecArray | null;
  while ((match = regex.exec(content)) !== null) {
    const code = match[1].trim();
    if (!code) {
      continue;
    }
    const variables = new Set<string>();
    try {
      extractAccessExpressions(code, variables);
    } catch (error) {
      const message = error instanceof Error ? error.message : String(error);
      throw new Error(`failed to parse binding expression ${JSON.stringify(code)}: ${message}`);
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
