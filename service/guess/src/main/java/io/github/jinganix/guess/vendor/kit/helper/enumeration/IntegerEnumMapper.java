// DO NOT EDIT: GENERATED FROM kit@6e62ecb9756df2aad5829162f26ebf8840a392ea by tool/vendor (toolVersion 2). Host edits belong outside vendor/kit. See vendor-lock.json.
package io.github.jinganix.guess.vendor.kit.helper.enumeration;

import io.github.jinganix.webpb.runtime.enumeration.Enumeration;

public class IntegerEnumMapper<T extends Enumeration<Integer>> extends EnumMapper<Integer, T> {

  public IntegerEnumMapper(T[] values) {
    super(values, Enumeration::getValue);
  }
}
