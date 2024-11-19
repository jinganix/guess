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

import * as Errors from "@helpers/errors";

describe("App", () => {
  describe("when launch", () => {
    it("then initialized", () => {
      const initialize = jest.fn();
      const mockInitializer = { appInitializer: { initialize } };
      jest.mock("@helpers/module/module.initializer", () => mockInitializer);

      const spyListenErrors = jest.spyOn(Errors, "listenErrors");

      // eslint-disable-next-line @typescript-eslint/no-require-imports
      require("./app");
      expect(initialize).toHaveBeenCalledWith("arg");
      expect(spyListenErrors).toHaveBeenCalledTimes(1);
    });
  });
});
