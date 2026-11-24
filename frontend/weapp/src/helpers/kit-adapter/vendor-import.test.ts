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
 *
 * https://github.com/jinganix/guess
 */

import { compactIds } from "../../vendor/kit/helpers/collection/compact-ids";
import { FilterAndSorter } from "../../vendor/kit/helpers/collection/filter-and-sorter";
import type { Filter } from "../../vendor/kit/helpers/collection/filter-and-sorter";
import {
  clampPage,
  pageNumbers,
  paginationData,
} from "../../vendor/kit/helpers/collection/pagination-numbers";
import type { TreeItem } from "../../vendor/kit/helpers/collection/tree-types";
import { collectTreeValues } from "../../vendor/kit/helpers/collection/tree-utils";
import { enumKey, enumValues } from "../../vendor/kit/helpers/utils/enum";
import { FakeRandom } from "../../vendor/kit/helpers/utils/fake-random";
import { Hash } from "../../vendor/kit/helpers/utils/hash";
import { SimpleLRUCache } from "../../vendor/kit/helpers/utils/lru-cache";
import { fromQuery, type QuerySource } from "../../vendor/kit/helpers/utils/query-params";
import { rgText } from "../../vendor/kit/helpers/utils/rg-text";

enum FixtureColor {
  Green = 1,
  Red = 2,
}

describe("vendored kit imports", () => {
  it("loads every vendored module with no fetch/document/localStorage at import time", () => {
    expect(typeof document).toBe("undefined");
    expect(compactIds(["a", "", null, undefined, "b"])).toEqual(["a", "b"]);

    const sorter = new FilterAndSorter<number>();
    const onlyEven: Filter<number> = {
      filter: (value: number): boolean => value % 2 === 0,
    };
    sorter.addFilter(onlyEven.filter, "even");
    expect(sorter.process([1, 2, 3])).toEqual([2]);

    expect(clampPage(Number.NaN, 5)).toBe(1);
    expect(pageNumbers(1, 3)).toEqual([1, 2, 3]);
    expect(paginationData(2, 3)).toHaveLength(3);

    const tree: TreeItem<string>[] = [
      {
        code: "root",
        items: [{ code: "leaf", label: "leaf", value: "leaf" }],
        label: "root",
        value: "root",
      },
    ];
    expect(collectTreeValues(tree)).toEqual(["root", "leaf"]);

    expect(enumValues<number[]>(FixtureColor)).toContain(1);
    expect(enumKey(FixtureColor, 1)).toBe("Green");

    expect(FakeRandom.nextInt(7)).toBe(Hash.hash32(7));

    const cache = new SimpleLRUCache<string, number>({ max: 10, ttl: 1000 });
    cache.set("k", 1);
    expect(cache.get("k")).toBe(1);

    const source: QuerySource = {
      get: (key: string): string | null => (key === "limit" ? "20" : null),
      has: (key: string): boolean => key === "limit",
    };
    expect(fromQuery({ limit: 10, q: "" }, source)).toEqual({ limit: 20, q: "" });

    expect(rgText(true)).toEqual({ "text-green-500": true, "text-red-500": false });
  });
});
