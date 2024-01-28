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

package io.github.jinganix.guess.service.module.moment.repository;

import io.github.jinganix.guess.service.module.moment.model.Moment;
import io.github.jinganix.guess.service.module.moment.model.MomentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Repository. */
@Repository
public interface MomentRepository extends CrudRepository<Moment, Long> {

  @Query(
      "SELECT m FROM Moment m WHERE "
          + "m.userId=?1 "
          + "AND m.status=io.github.jinganix.guess.service.module.moment.model.MomentStatus.ACTIVE")
  List<Moment> findAllByUserId(Long userId, Pageable pageable);

  @Query(
      "SELECT m FROM Moment m WHERE "
          + "m.userId=?1 "
          + "AND m.status=io.github.jinganix.guess.service.module.moment.model.MomentStatus.ACTIVE "
          + "AND m.createdAt<?2 "
          + "OR (m.createdAt=?2 AND m.id<?3)")
  List<Moment> findAllByUserIdAndCursor(Long userId, Long createdAt, Long id, Pageable pageable);

  @Query(
      "SELECT m FROM Moment m WHERE "
          + "m.status=io.github.jinganix.guess.service.module.moment.model.MomentStatus.ACTIVE")
  List<Moment> findAll(Pageable pageable);

  @Query(
      "SELECT m FROM Moment m WHERE "
          + "m.status=io.github.jinganix.guess.service.module.moment.model.MomentStatus.ACTIVE "
          + "AND m.createdAt<?1 "
          + "OR (m.createdAt=?1 AND m.id<?2)")
  List<Moment> findAllByCursor(Long createdAt, Long id, Pageable pageable);

  @Modifying
  @Query("UPDATE Moment m SET m.status=?2, m.updatedAt=?3 WHERE m.id=?1")
  void setStatusById(Long id, MomentStatus status, long millis);

  Optional<Moment> findFirstByUserIdOrderByCreatedAtDesc(Long userId);

  @Modifying
  @Transactional
  @Query("UPDATE Moment m SET m.follow=?2, m.updatedAt=?3 WHERE m.id=?1")
  void setFollow(Long momentId, int count, long millis);

  @Modifying
  @Transactional
  @Query("UPDATE Moment m SET m.follow=m.follow+1, m.updatedAt=?2 WHERE m.id=?1")
  void incrFollow(Long momentId, long millis);

  @Modifying
  @Transactional
  @Query("UPDATE Moment m SET m.follow=m.follow-1, m.updatedAt=?2 WHERE m.id=?1")
  void decrFollow(Long momentId, long millis);

  @Modifying
  @Transactional
  @Query("UPDATE Moment m SET m.comment=?2, m.updatedAt=?3 WHERE m.id=?1")
  void setComment(Long momentId, int count, long millis);

  @Modifying
  @Transactional
  @Query("UPDATE Moment m SET m.comment=m.comment+1, m.updatedAt=?2 WHERE m.id=?1")
  void incrComment(Long momentId, long millis);

  @Modifying
  @Transactional
  @Query("UPDATE Moment m SET m.comment=m.comment-1, m.updatedAt=?2 WHERE m.id=?1")
  void decrComment(Long momentId, long millis);

  @Modifying
  @Transactional
  @Query("UPDATE Moment m SET m.like=m.like+1, m.updatedAt=?2 WHERE m.id=?1")
  void incrLike(Long momentId, long millis);

  @Modifying
  @Transactional
  @Query("UPDATE Moment m SET m.like=m.like-1, m.updatedAt=?2 WHERE m.id=?1")
  void decrLike(Long momentId, long millis);
}
