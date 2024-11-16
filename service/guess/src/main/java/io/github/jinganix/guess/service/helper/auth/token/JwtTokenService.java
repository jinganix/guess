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

package io.github.jinganix.guess.service.helper.auth.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JwtTokenService implements TokenService {

  private static final String USER_ID = "uid";

  private static final String TOKEN = "tk";

  private static final String SCOPES = "sco";

  private final String issuer;

  private final Algorithm algorithm;

  private final JWTVerifier verifier;

  public JwtTokenService(
      @Value("${core.issuer}") String issuer, @Value("${core.jwt-secret}") String jwtSecret) {
    this.issuer = issuer;
    this.algorithm = Algorithm.HMAC256(jwtSecret);
    this.verifier = JWT.require(algorithm).withIssuer(issuer).build();
  }

  @Override
  public String generate(Long userId, String uuid, String... scopes) {
    return JWT.create()
        .withArrayClaim(SCOPES, scopes)
        .withClaim(USER_ID, userId)
        .withClaim(TOKEN, uuid)
        .withIssuedAt(new Date())
        .withIssuer(issuer)
        .sign(algorithm);
  }

  public JwtToken decode(String text) {
    try {
      DecodedJWT jwt = verifier.verify(text);
      if (jwt.getIssuedAt().getTime() < System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)) {
        return null;
      }
      List<String> authorities = jwt.getClaim(SCOPES).asList(String.class);
      return JwtToken.of(
          jwt.getClaim(USER_ID).asLong(),
          jwt.getClaim(TOKEN).asString(),
          authorities == null ? Collections.emptyList() : authorities);
    } catch (Exception e) {
      log.error("token: " + text + " error: " + e.getMessage());
      return null;
    }
  }
}
