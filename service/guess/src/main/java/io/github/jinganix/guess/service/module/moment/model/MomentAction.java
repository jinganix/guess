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

package io.github.jinganix.guess.service.module.moment.model;

import io.github.jinganix.guess.service.helper.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class MomentAction extends AbstractEntity {

  @Id private Long id;

  private Long userId;

  private Long momentId;

  @Column(columnDefinition = "tinyint")
  private boolean correct;

  @Column(columnDefinition = "tinyint")
  private boolean followed;

  @Column(columnDefinition = "tinyint")
  private boolean liked;

  @Column(columnDefinition = "tinyint")
  private boolean reported;
}
