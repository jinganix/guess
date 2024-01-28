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

package io.github.jinganix.guess.service.module.comment.repository;

import io.github.jinganix.guess.service.module.comment.model.Comment;
import io.github.jinganix.guess.service.module.comment.model.CommentStatus;
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
public interface CommentRepository extends CrudRepository<Comment, Long> {

  @Query(
      "SELECT c FROM Comment c WHERE "
          + "c.momentId=?1 "
          + "AND c.status=io.github.jinganix.guess.service.module.comment.model.CommentStatus.ACTIVE ")
  List<Comment> findAll(Long momentId, Pageable pageable);

  @Query(
      "SELECT c FROM Comment c WHERE "
          + "c.momentId=?1 "
          + "AND c.status=io.github.jinganix.guess.service.module.comment.model.CommentStatus.ACTIVE "
          + "AND c.createdAt<?2 "
          + "OR (c.createdAt=?2 AND c.id<?3)")
  List<Comment> findAllByCursor(Long momentId, Long createdAt, Long commentId, Pageable pageable);

  @Query(
      "SELECT c FROM Comment c WHERE "
          + "c.createdAt<?1 "
          + "OR (c.createdAt=?1 AND c.id<?2) "
          + "AND c.status=io.github.jinganix.guess.service.module.comment.model.CommentStatus.ACTIVE "
          + "ORDER BY c.createdAt DESC")
  List<Comment> findAllByCursor(Long createdAt, Long id, Pageable pageable);

  @Query(
      "SELECT c FROM Comment c WHERE "
          + "c.status=io.github.jinganix.guess.service.module.comment.model.CommentStatus.ACTIVE "
          + "ORDER BY c.createdAt DESC")
  List<Comment> findAll(Pageable pageable);

  Integer countByMomentIdAndStatus(Long momentId, CommentStatus status);

  Optional<Comment> findFirstByUserIdOrderByCreatedAtDesc(Long userId);

  @Modifying
  @Transactional
  @Query("UPDATE Comment c SET c.status=?2, c.updatedAt=?3 WHERE c.id=?1")
  void setStatusById(Long id, CommentStatus status, long millis);

  @Modifying
  @Transactional
  @Query("UPDATE Comment c SET c.like=c.like+1, c.updatedAt=?2 WHERE c.id=?1")
  void incrLike(Long commentId, long millis);

  @Modifying
  @Transactional
  @Query("UPDATE Comment c SET c.like=c.like-1, c.updatedAt=?2 WHERE c.id=?1")
  void decrLike(Long commentId, long millis);
}
