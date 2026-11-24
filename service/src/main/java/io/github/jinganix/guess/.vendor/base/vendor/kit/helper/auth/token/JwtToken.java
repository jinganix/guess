// DO NOT EDIT: GENERATED FROM kit@6e62ecb9756df2aad5829162f26ebf8840a392ea by tool/vendor (toolVersion 2). Host edits belong outside vendor/kit. See vendor-lock.json.
package io.github.jinganix.guess.vendor.kit.helper.auth.token;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class JwtToken {

  public static final JwtToken INVALID_TOKEN = new JwtToken(null, null, Collections.emptyList());

  private final Long userId;

  private final String uuid;

  private final List<String> authorities;

  public boolean isValid() {
    return uuid != null;
  }
}
