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

import { PuzzleDetailScript } from "@pages/puzzle/detail/script";
import { cast } from "@helpers/utils/utils";
import { configStore } from "../../../modules/container";

describe("PuzzleDetailScript", () => {
  beforeEach(() => jest.useFakeTimers());

  afterEach(() => jest.clearAllTimers());

  describe("classId", () => {
    describe("when called", () => {
      it("then return a class Id", () => {
        const script = new PuzzleDetailScript(cast({}));
        expect(script.classId()).not.toBeNull();
      });
    });
  });

  describe("source", () => {
    describe("when called", () => {
      it("then return", () => {
        const script = new PuzzleDetailScript(cast({}));
        const source = script.source();
        expect(source.configStore).toEqual(configStore);
        expect(source.store).toEqual(script);
      });
    });
  });
});
