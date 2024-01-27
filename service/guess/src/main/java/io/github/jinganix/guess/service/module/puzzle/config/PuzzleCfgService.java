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

package io.github.jinganix.guess.service.module.puzzle.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jinganix.guess.service.helper.fake.FakeRandom;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class PuzzleCfgService {

  private final LinkedHashMap<Integer, PuzzleCfg> cfgMap = new LinkedHashMap<>();

  private final MultiValueMap<Integer, String> answersMap = new LinkedMultiValueMap<>();

  private PuzzleCfgService() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    String filename = "/config/puzzle.json";
    try (InputStream stream = PuzzleCfgService.class.getResourceAsStream(filename)) {
      if (stream == null) {
        throw new RuntimeException("Can not open file: " + filename);
      }
      List<PuzzleCfg> cfgList = objectMapper.readValue(stream, new TypeReference<>() {});
      Collections.shuffle(cfgList, new Random(cfgList.size()));
      for (int i = 0; i < cfgList.size(); i++) {
        PuzzleCfg cfg = cfgList.get(i);
        cfg.setId(i + 1);
        cfgMap.put(cfg.getId(), cfg);
        answersMap.add(cfg.getType(), cfg.getAnswer());
      }
    }
  }

  public PuzzleCfg getCfg(Integer id) {
    return cfgMap.get(id);
  }

  public String randomAnswer(Integer type, long seed) {
    List<String> answers = answersMap.get(type);
    return answers.get(FakeRandom.nextInt(seed, 0, answers.size()));
  }
}
