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

import { AuthToken } from "@helpers/token";
import { createTinyEvents } from "@helpers/event/events";

export type NotifierVariant = "info" | "error" | "success";

export interface Emitter {
  errorCode: (code: number) => void;
  loading: (loading: boolean, type: string) => void;
  location: (path: string) => void;
  notifier: (variant: NotifierVariant, cont: string) => void;
  request: (route: string, payload: string) => void;
  token: (token: AuthToken | null) => void;
}

export const emitter = createTinyEvents<Emitter>();
