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

package io.github.jinganix.guess.service.module.moment;

import io.github.jinganix.guess.proto.service.moment.MomentActionPb;
import io.github.jinganix.guess.proto.service.moment.MomentCreateRequest;
import io.github.jinganix.guess.proto.service.moment.MomentDetailPb;
import io.github.jinganix.guess.proto.service.moment.MomentPb;
import io.github.jinganix.guess.service.module.moment.model.Moment;
import io.github.jinganix.guess.service.module.moment.model.MomentAction;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class MomentMapper {

  public abstract MomentActionPb mapToPb(MomentAction action);

  public abstract MomentPb mapToPb(Moment moment);

  @Mapping(target = "answer", source = "answer")
  public abstract MomentDetailPb mapToPb(Moment moment, MomentAction action, int answer);

  public abstract void update(@MappingTarget Moment moment, MomentCreateRequest request);
}
