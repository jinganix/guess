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

package io.github.jinganix.guess.service.module.puzzle;

import io.github.jinganix.guess.proto.service.puzzle.PuzzlePb;
import io.github.jinganix.guess.service.module.puzzle.config.PuzzleCfg;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

/** Mapper for proto messages. */
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PuzzleMapper {

  PuzzlePb mapToPb(PuzzleCfg cfg, List<String> options, Integer limit);
}
