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

import { fromQuery, type QuerySource } from "../../vendor/kit/helpers/utils/query-params";

/**
 * Weapp page `onLoad(options)` carries query params as a plain string record,
 * not a `URLSearchParams` (unavailable on low base libs). This adapts the
 * record to the `{ has, get }` subset `fromQuery` needs and overlays it onto
 * a defaults object with number/boolean coercion.
 */
export function fromPageOptions<T extends Record<string, unknown>>(
  defaults: T,
  options?: Record<string, string | undefined>,
): T {
  if (!options) {
    return fromQuery(defaults);
  }
  const source: QuerySource = {
    get: (key: string): string | null => {
      const value = options[key];
      return value === undefined ? null : value;
    },
    has: (key: string): boolean => options[key] !== undefined,
  };
  return fromQuery(defaults, source);
}
