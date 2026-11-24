const path = require("path");
const loader = require("./index");

const fixture = (...parts) => path.join(__dirname, "__fixtures__", ...parts);

function runLoader(source, resourcePath, options = {}) {
  const warnings = [];
  const deps = [];
  let result;
  let error;
  const context = {
    addDependency: (dep) => deps.push(dep),
    async: () => (err, out) => {
      error = err;
      result = out;
    },
    emitWarning: (warning) => warnings.push(String(warning)),
    getOptions: () => options,
    query: options,
    resourcePath,
  };
  loader.call(context, source);
  if (error) {
    throw error;
  }
  return { deps, result, warnings };
}

describe("pick-loader", () => {
  it("injects picks from the sibling index.wxml into super(comp)", () => {
    const source = `export class Foo extends ComponentScript {
  constructor(comp) { super(comp); }
}`;
    const { deps, result, warnings } = runLoader(source, fixture("basic", "script.ts"));
    expect(result).toContain(`super(comp, ["show","title"])`);
    expect(deps).toEqual([fixture("basic", "index.wxml")]);
    expect(warnings).toEqual([]);
  });

  it("leaves explicit picks untouched", () => {
    const source = `constructor(comp) { super(comp, ["a"]); }`;
    const { deps, result } = runLoader(source, fixture("basic", "script.ts"));
    expect(result).toBe(source);
    expect(deps).toEqual([]);
  });

  it("leaves the file untouched when index.wxml is missing (runtime falls back to full bind)", () => {
    const source = `constructor(comp) { super(comp); }`;
    const { result, warnings } = runLoader(source, fixture("missing", "script.ts"));
    expect(result).toBe(source);
    expect(warnings).toEqual([]);
  });

  it("warns and leaves the file untouched on unparsable expressions", () => {
    const source = `constructor(comp) { super(comp); }`;
    const { deps, result, warnings } = runLoader(source, fixture("unparsable", "script.ts"));
    expect(result).toBe(source);
    expect(warnings).toHaveLength(1);
    expect(warnings[0]).toMatch(/a %% b/);
    expect(deps).toEqual([fixture("unparsable", "index.wxml")]);
  });

  it("does not treat $ in picks as replacement patterns", () => {
    const source = `constructor(comp) { super(comp); }`;
    const { result } = runLoader(source, fixture("dollar", "script.ts"));
    expect(result).toContain(`super(comp, ["a$$b"])`);
  });

  it("skips injection with the fullBind option", () => {
    const source = `constructor(comp) { super(comp); }`;
    const { result } = runLoader(source, fixture("basic", "script.ts"), { fullBind: true });
    expect(result).toBe(source);
  });

  it("treats only FULL_BIND=1/true as enabled", () => {
    process.env.FULL_BIND = "0";
    try {
      const source = `constructor(comp) { super(comp); }`;
      const { result } = runLoader(source, fixture("basic", "script.ts"));
      expect(result).toContain(`super(comp, ["show","title"])`);
    } finally {
      delete process.env.FULL_BIND;
    }
  });

  it("skips injection with the FULL_BIND env switch", () => {
    process.env.FULL_BIND = "1";
    try {
      const source = `constructor(comp) { super(comp); }`;
      const { result } = runLoader(source, fixture("basic", "script.ts"));
      expect(result).toBe(source);
    } finally {
      delete process.env.FULL_BIND;
    }
  });

  it("fails hard on unparsable expressions with STRICT_PICK=1", () => {
    process.env.STRICT_PICK = "1";
    try {
      const source = `constructor(comp) { super(comp); }`;
      expect(() => runLoader(source, fixture("unparsable", "script.ts"))).toThrow(/a %% b/);
    } finally {
      delete process.env.STRICT_PICK;
    }
  });

  it("rewrites only the first super(comp) call", () => {
    const source = `class A { constructor(comp) { super(comp); } }
class B { constructor(comp) { super(comp); } }`;
    const { result } = runLoader(source, fixture("basic", "script.ts"));
    expect(result.indexOf(`super(comp, ["show","title"])`)).toBeGreaterThan(-1);
    expect(result).toContain("class B { constructor(comp) { super(comp); } }");
  });
});
