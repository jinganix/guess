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

import { LaunchShowOption } from "@helpers/wx/wx.types";
import { appInitializer } from "@helpers/module/module.initializer";
import { Dispose } from "@helpers/types/types";
import { listenErrors } from "@helpers/errors";

App<{ disposes: Dispose[] }>({
  disposes: [],

  onLaunch(option: LaunchShowOption) {
    appInitializer.initialize(option);
    this.disposes = [listenErrors(), appInitializer.onAppDispose()];
  },
});
