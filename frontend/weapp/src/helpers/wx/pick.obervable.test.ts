import { pickObservable } from "./pick.obervable";

describe("PickObservable", () => {
  [
    { data: null, expected: {}, paths: ["a"] },
    { data: undefined, expected: {}, paths: ["a"] },
    { data: "", expected: {}, paths: ["a"] },
    { data: [], expected: {}, paths: ["a"] },
    { data: 0, expected: {}, paths: ["a"] },
    { data: { a: 1 }, expected: { a: 1 }, paths: ["a"] },
    { data: { a: 1 }, expected: {}, paths: ["b", "b.c"] },
    { data: { a: { b: 1, c: 2 } }, expected: { a: { b: 1, c: 2 } }, paths: ["a"] },
    { data: { a: { b: 1, c: 2 } }, expected: { a: { b: 1 } }, paths: ["a.b"] },
    { data: { a: { b: 1, c: 2 } }, expected: { a: { b: 1, c: 2 } }, paths: ["a.b", "a.c"] },
    { data: { a: null }, expected: { a: null }, paths: ["a.b"] },
    { data: { a: 1 }, expected: {}, paths: ["b"] },
    { data: { a: [] }, expected: { a: [] }, paths: ["a"] },
    { data: { a: [] }, expected: { a: [] }, paths: ["a.length"] },
    { data: { a: [] }, expected: { a: [] }, paths: ["a", "a.length"] },
    { data: { a: { b: [] } }, expected: { a: { b: [] } }, paths: ["a.b", "a.b.length"] },
  ].forEach(({ data, paths, expected }) => {
    it(`pickObservable(${JSON.stringify(data)}, ${JSON.stringify(paths)} => ${JSON.stringify(expected)}`, () => {
      expect(pickObservable(data, paths)).toStrictEqual(expected);
    });
  });
});
