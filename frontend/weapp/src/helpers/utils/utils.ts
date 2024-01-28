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

import fill from "lodash/fill";
import dayjs from "dayjs";
import { query as formatQuery } from "webpb";

export function cast<T>(obj: unknown): T {
  return obj as T;
}

export function isBlank(str: string): boolean {
  return !str || /^\s*$/.test(str);
}

export function trimContent(text: string): string {
  text = text ?? "";
  if (isBlank(text)) {
    return "";
  }
  return text.replace(/[\r\n\s\t]+$/, "");
}

export function relativePrefix(route: string): string {
  const size = route.split("/").length - 1;
  return fill(Array(size), "../").join("");
}

export function formatReadable(time: number): string {
  if (!time) {
    return "";
  }
  const dayTime = dayjs(time);
  const dayNow = dayjs(Date.now());
  return dayTime.isSame(dayNow, "day")
    ? dayTime.format("HH:mm")
    : `${(toDate(Date.now()) - toDate(time)) / 86400000}天前`;
}

export function toDate(time: number): number {
  return new Date(time).setHours(0, 0, 0, 0);
}

export function formatUrl(path: string, query: Record<string, unknown>): string {
  return `${path}${formatQuery("?", query)}`;
}

let radix = 0;
export function classId(): string {
  return (radix++).toString(32);
}
