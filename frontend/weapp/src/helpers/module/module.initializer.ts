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

import { Replay } from "@helpers/promise/replay";
import { Dispose, ModuleInitializer } from "@helpers/types/types";
import { deleteAuthToken } from "@helpers/token";
import { configStore, userExtraStore, userStore } from "@modules/container";
import { LaunchShowOption } from "@helpers/wx/wx.types";

const INITIALIZERS: ModuleInitializer[][] = [[userStore], [configStore, userExtraStore]];

export class ModuleInitializerImpl implements ModuleInitializer {
  replay = new Replay<void>();
  disposes: Dispose[] = [];

  dispose(): void {
    this.replay = new Replay<void>();
    this.disposes.forEach((dispose) => dispose());
  }

  async initialize(): Promise<Dispose[]> {
    await this.replay.resolve(async () => {
      let stage = 0;
      const disposes: Dispose[] = [];
      for (const initializers of INITIALIZERS) {
        console.log(`····> [${++stage}] initializing`);
        const disposesList = await Promise.all(initializers.map((x) => x.initialize()));
        disposesList.forEach((x) => disposes.push(...x));
        console.log(`====> [${stage}] initialized`);
      }
      this.disposes = disposes.reverse();
    });
    return [() => this.dispose()];
  }

  fetchData(): Promise<void> {
    return Promise.resolve();
  }
}

const initializer = new ModuleInitializerImpl();

export async function initializeModules(): Promise<Dispose[]> {
  return initializer ? await initializer.initialize() : Promise.resolve([]);
}

export async function tryInitializeModules(attempts: number = 2): Promise<boolean> {
  if (!attempts) {
    return false;
  }
  try {
    await initializeModules();
    return true;
  } catch (err) {
    await deleteAuthToken();
    return await tryInitializeModules(attempts - 1);
  }
}

export class AppInitializer {
  initialize(options: LaunchShowOption): void {
    const scene = (options as { scene: number }).scene;
    const preview = scene === 1154;
    configStore.updatePreview(preview);
    if (!preview) {
      void tryInitializeModules(2);
    }
  }
}

export const appInitializer = new AppInitializer();
