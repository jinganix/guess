// DO NOT EDIT: GENERATED FROM kit@6e62ecb9756df2aad5829162f26ebf8840a392ea by tool/vendor (toolVersion 2). Host edits belong outside vendor/kit. See vendor-lock.json.
package io.github.jinganix.guess.vendor.kit.setup.argument;

import java.lang.annotation.Annotation;
import lombok.experimental.UtilityClass;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;

@UtilityClass
public class AnnotationFinder {

  public static <T extends Annotation> T findMethodAnnotation(
      Class<T> annotationClass, MethodParameter parameter) {
    T annotation = parameter.getParameterAnnotation(annotationClass);
    if (annotation != null) {
      return annotation;
    }
    Annotation[] annotationsToSearch = parameter.getParameterAnnotations();
    for (Annotation toSearch : annotationsToSearch) {
      annotation = AnnotationUtils.findAnnotation(toSearch.annotationType(), annotationClass);
      if (annotation != null) {
        return annotation;
      }
    }
    return null;
  }
}
