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

import io.github.jinganix.guess.service.module.moment.model.MomentAction;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** Repository. */
@Repository
public interface MomentActionRepository extends CrudRepository<MomentAction, Long> {

  MomentAction findByUserIdAndMomentId(Long userId, Long momentId);

  @Query("SELECT f FROM MomentAction f WHERE " + "f.userId=?1 " + "AND f.followed=true")
  List<MomentAction> findAllByUserId(Long userId, Pageable pageable);

  List<MomentAction> findAllByUserIdAndMomentIdIn(Long userId, Collection<Long> momentIds);

  @Query(
      "SELECT f FROM MomentAction f WHERE "
          + "f.userId=?1 "
          + "AND f.followed=true "
          + "AND f.createdAt<?2 "
          + "OR (f.createdAt=?2 AND f.id<?3)")
  List<MomentAction> findAllByUserIdAndCursor(Long userId, Long createdAt, Long id, Pageable p);
}
