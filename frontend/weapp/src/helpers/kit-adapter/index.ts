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

/** Vendored kit pure logic, re-exported for weapp consumers (read-only mirror). */
export { compactIds } from "../../vendor/kit/helpers/collection/compact-ids";
export { FilterAndSorter } from "../../vendor/kit/helpers/collection/filter-and-sorter";
export type { Filter, Sorter } from "../../vendor/kit/helpers/collection/filter-and-sorter";
export {
  PAGINATION_ELLIPSIS,
  clampPage,
  pageNumbers,
  paginationData,
} from "../../vendor/kit/helpers/collection/pagination-numbers";
export type { PaginationItem } from "../../vendor/kit/helpers/collection/pagination-numbers";
export type { TreeItem } from "../../vendor/kit/helpers/collection/tree-types";
export { collectTreeValues } from "../../vendor/kit/helpers/collection/tree-utils";
export {
  constEnumOptions,
  enumKey,
  enumName,
  enumToOptions,
  enumValueOptions,
  enumValues,
  invertEnumNameMap,
} from "../../vendor/kit/helpers/utils/enum";
export type { Option } from "../../vendor/kit/helpers/utils/enum";
export { FakeRandom } from "../../vendor/kit/helpers/utils/fake-random";
export { Hash } from "../../vendor/kit/helpers/utils/hash";
export { SimpleLRUCache } from "../../vendor/kit/helpers/utils/lru-cache";
export type { SimpleLRUCacheOptions } from "../../vendor/kit/helpers/utils/lru-cache";
export { fromQuery } from "../../vendor/kit/helpers/utils/query-params";
export { rgText } from "../../vendor/kit/helpers/utils/rg-text";
export { fromPageOptions } from "./page-query";
