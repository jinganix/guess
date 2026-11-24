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

import { fromPageOptions } from "@helpers/kit-adapter/page-query";

describe("fromPageOptions", () => {
  it("overlays weapp onLoad options with number/boolean coercion", () => {
    const result = fromPageOptions(
      { limit: 10, q: "", refresh: false },
      {
        limit: "20",
        q: "kit",
        refresh: "true",
      },
    );
    expect(result).toEqual({ limit: 20, q: "kit", refresh: true });
  });

  it("keeps defaults for missing keys and ignores unknown keys", () => {
    const result = fromPageOptions({ limit: 10, q: "" }, { extra: "1" });
    expect(result).toEqual({ limit: 10, q: "" });
  });

  it("returns defaults when options are absent", () => {
    expect(fromPageOptions({ limit: 10 })).toEqual({ limit: 10 });
  });
});
