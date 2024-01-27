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

package io.github.jinganix.guess.service.module.user.repository;

import io.github.jinganix.guess.service.module.user.model.UserExtra;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Repository. */
@Repository
public interface UserExtraRepository extends CrudRepository<UserExtra, Long> {

  @Modifying
  @Transactional
  @Query("UPDATE UserExtra u SET u.follow=u.follow+1, u.updatedAt=?2 WHERE u.id=?1")
  void incrFollow(Long userId, long millis);

  @Modifying
  @Transactional
  @Query("UPDATE UserExtra u SET u.follow=u.follow-1, u.updatedAt=?2 WHERE u.id=?1")
  void decrFollow(Long userId, long millis);

  @Modifying
  @Transactional
  @Query("UPDATE UserExtra u SET u.moment=u.moment+1, u.updatedAt=?2 WHERE u.id=?1")
  void incrMoment(Long userId, long millis);

  @Modifying
  @Transactional
  @Query("UPDATE UserExtra u SET u.moment=u.moment-1, u.updatedAt=?2 WHERE u.id=?1")
  void decrMoment(Long userId, long millis);
}
