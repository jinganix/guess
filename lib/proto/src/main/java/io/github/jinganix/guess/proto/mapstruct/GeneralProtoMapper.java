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

package io.github.jinganix.guess.proto.mapstruct;

import io.github.jinganix.guess.proto.imports.common.PageablePb;
import io.github.jinganix.guess.proto.imports.common.PagingPb;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** General mapstruct mapper. */
@RequiredArgsConstructor
public class GeneralProtoMapper {

  private final SpringDataWebProperties dataWebProperties;

  /**
   * {@link PagingPb} mapper.
   *
   * @param page {@link Page}
   * @return {@link PagingPb}
   */
  public PagingPb pagingPb(Page<?> page) {
    if (page.getPageable().isPaged()) {
      return new PagingPb()
          .setPage(
              dataWebProperties.getPageable().isOneIndexedParameters()
                  ? page.getNumber() + 1
                  : page.getNumber())
          .setSize(page.getSize())
          .setTotalCount((int) page.getTotalElements())
          .setTotalPage(page.getTotalPages());
    }
    return null;
  }

  /**
   * {@link PageablePb} mapper.
   *
   * @param pageable {@link Pageable}
   * @return {@link PageablePb}
   */
  public PageablePb pageablePb(Pageable pageable) {
    if (pageable == null) {
      return null;
    }
    return new PageablePb()
        .setPage(pageable.getPageNumber())
        .setSize(pageable.getPageSize())
        .setPagination(true)
        .setSort(pageable.getSort().toString());
  }
}
