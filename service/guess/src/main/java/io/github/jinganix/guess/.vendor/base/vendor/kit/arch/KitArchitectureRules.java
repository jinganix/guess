// DO NOT EDIT: GENERATED FROM kit@6e62ecb9756df2aad5829162f26ebf8840a392ea by tool/vendor (toolVersion 2). Host edits belong outside vendor/kit. See vendor-lock.json.
package io.github.jinganix.guess.vendor.kit.arch;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaCodeUnit;
import com.tngtech.archunit.core.domain.JavaConstructorCall;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.domain.JavaParameterizedType;
import com.tngtech.archunit.core.domain.JavaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Shared ArchUnit layering rules for {@code Controller -> Handler -> Service -> Repository}.
 *
 * <p>Leaf module (no kit dependencies) so every service module — including {@code tenant},
 * which {@code :service:test-support} already depends on — can reuse it without project
 * cycles. Each service module keeps its own {@code *ArchitectureTest} (own scan root +
 * display names) and delegates to these methods so new MUSTs land once here instead of
 * being copy-pasted per module. Host repos reuse the same methods against their own root
 * package (see {@code setup.md} §4.1).
 */
public final class KitArchitectureRules {

  private KitArchitectureRules() {}

  /**
   * Controllers delegate to handlers only: no {@code *Service} / {@code *Repository} deps, no
   * {@code *Mapper} fields (see {@code spring/service.md} §分层 + {@code
   * host-service-layout.flow.md}).
   */
  public static void checkControllersThin(JavaClasses classes) {
    noClasses()
        .that()
        .areAnnotatedWith(RestController.class)
        .should()
        .dependOnClassesThat()
        .haveSimpleNameEndingWith("Service")
        .allowEmptyShould(true).check(classes);
    noClasses()
        .that()
        .areAnnotatedWith(RestController.class)
        .should()
        .dependOnClassesThat()
        .haveSimpleNameEndingWith("Repository")
        .allowEmptyShould(true).check(classes);
    noFields()
        .that()
        .areDeclaredInClassesThat()
        .areAnnotatedWith(RestController.class)
        .should()
        .haveRawType(JavaClass.Predicates.simpleNameEndingWith("Mapper"))
        .allowEmptyShould(true).check(classes);
  }

  /** Handlers never reach back into controllers. */
  /** Handlers never depend back on controllers (see {@code spring/service.md} §分层). */
  public static void checkHandlersFreeOfControllers(JavaClasses classes) {
    noClasses()
        .that()
        .resideInAPackage("..handler..")
        .should()
        .dependOnClassesThat()
        .areAnnotatedWith(RestController.class)
        .allowEmptyShould(true).check(classes);
  }

  /**
   * Services orchestrate domain logic: no deps on {@code handler/} or controllers ({@code
   * *Service} names; Impl variants see {@link #checkServiceImplsFreeOfHandlerAndController}
   * and {@link #checkServiceImplsOnlyDependOnSameModuleHandlers}). See {@code
   * spring/service.md} §分层.
   */
  public static void checkServicesFreeOfHandlerAndController(JavaClasses classes) {
    noClasses()
        .that()
        .haveSimpleNameEndingWith("Service")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..handler..")
        .allowEmptyShould(true).check(classes);
    noClasses()
        .that()
        .haveSimpleNameEndingWith("Service")
        .should()
        .dependOnClassesThat()
        .areAnnotatedWith(RestController.class)
        .allowEmptyShould(true).check(classes);
  }

  /**
   * {@code *ServiceImpl} variants obey the same direction (the {@code *Service} name check
   * alone misses them). Game modules without HTTP surface use {@link
   * #checkServiceImplsOnlyDependOnSameModuleHandlers} instead. See {@code spring/service.md}
   * §分层.
   */
  public static void checkServiceImplsFreeOfHandlerAndController(JavaClasses classes) {
    noClasses()
        .that()
        .haveSimpleNameEndingWith("ServiceImpl")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..handler..")
        .allowEmptyShould(true).check(classes);
    noClasses()
        .that()
        .haveSimpleNameEndingWith("ServiceImpl")
        .should()
        .dependOnClassesThat()
        .areAnnotatedWith(RestController.class)
        .allowEmptyShould(true).check(classes);
  }

  /**
   * Game-style api facade: a {@code *ServiceImpl} in {@code internal/} may delegate to
   * {@code handler/} classes of the <em>same</em> module subtree only (Modulith exports
   * {@code api}, never {@code internal.handler}, so hosts call the facade; e.g. {@code
   * game.chat.internal.ChatServiceImpl} → {@code ..chat.internal.handler..}).
   * Cross-module handler wiring and any controller dependency still fail.
   *
   * <p>{@code *ServiceImpl} stays inside its own module: no controller deps, and handler deps only
   * from the same module (see {@code spring/service.md} §分层 + {@code
   * system-module-layout.flow.md}).
   *
   * @param domainRootPackage the module root (e.g. {@code io.github.jinganix.guess.vendor.kit.game});
   *     the module segment is the first segment below it
   */
  public static void checkServiceImplsOnlyDependOnSameModuleHandlers(
      JavaClasses classes, String domainRootPackage) {
    noClasses()
        .that()
        .haveSimpleNameEndingWith("ServiceImpl")
        .should()
        .dependOnClassesThat()
        .areAnnotatedWith(RestController.class)
        .allowEmptyShould(true).check(classes);
    classes()
        .that()
        .haveSimpleNameEndingWith("ServiceImpl")
        .and()
        .resideInAPackage(domainRootPackage + "..")
        .should(onlySameModuleHandlers(domainRootPackage))
        .allowEmptyShould(true).check(classes);
  }

  /**
   * Services take domain types, never proto {@code *Request} / {@code *Response} envelopes
   * (those stop at Handler; see {@code spring/service.md}).
   *
   * @param domainPatterns ArchUnit package patterns scoping the domain under test
   */
  public static void checkServicesFreeOfProtoEnvelopes(
      JavaClasses classes, String... domainPatterns) {
    methods()
        .that()
        .areDeclaredInClassesThat(
            serviceClasses().and(
                new DescribedPredicate<JavaClass>("reside in domain") {
                  @Override
                  public boolean test(JavaClass javaClass) {
                    return matchesAnyPackage(
                        javaClass.getPackageName(), domainPatterns);
                  }
                }))
        .should(notTakeProtoEnvelopes())
        .allowEmptyShould(true).check(classes);
  }

  private static DescribedPredicate<JavaClass> serviceClasses() {
    return JavaClass.Predicates.simpleNameEndingWith("Service")
        .or(JavaClass.Predicates.simpleNameEndingWith("ServiceImpl"))
        .as("have simple name ending with Service/ServiceImpl");
  }

  private static ArchCondition<JavaClass> onlySameModuleHandlers(String domainRootPackage) {
    return new ArchCondition<JavaClass>("depend only on same-module handlers") {
      @Override
      public void check(JavaClass serviceImpl, ConditionEvents events) {
        String moduleRoot = moduleRootOf(serviceImpl.getPackageName(), domainRootPackage);
        if (moduleRoot == null) {
          return;
        }
        for (Dependency dependency : serviceImpl.getDirectDependenciesFromSelf()) {
          JavaClass target = dependency.getTargetClass();
          if (!target.getPackageName().contains(".handler.")) {
            continue;
          }
          if (!target.getPackageName().startsWith(moduleRoot)) {
            events.add(
                SimpleConditionEvent.violated(
                    serviceImpl,
                    serviceImpl.getSimpleName()
                        + " depends on cross-module handler "
                        + target.getFullName()
                        + " (same-module facade only)"));
          }
        }
      }
    };
  }

  private static ArchCondition<JavaMethod> notTakeProtoEnvelopes() {
    return new ArchCondition<JavaMethod>("not take proto Request/Response parameters") {
      @Override
      public void check(JavaMethod method, ConditionEvents events) {
        for (JavaClass param : method.getRawParameterTypes()) {
          if (param.getPackageName().contains(".proto.")
              && (param.getSimpleName().endsWith("Request")
                  || param.getSimpleName().endsWith("Response"))) {
            events.add(
                SimpleConditionEvent.violated(
                    method,
                    method.getFullName()
                        + " takes proto envelope "
                        + param.getFullName()
                        + " (envelopes stop at Handler)"));
          }
        }
      }
    };
  }

  /**
   * Injected bean fields are named after their type (decapitalized simple name; see {@code
   * spring/service.md} §依赖注入 ): {@code SupportService → supportService}. Allowed
   * deviations only:
   *
   * <ul>
   *   <li>spec 高频常用简化名: {@code dsl} ({@code DSLContext}), {@code recordMapper} ({@code
   *       *RecordMapper}) — unique codebase-wide;
   *   <li>generic wrappers ({@code ObjectProvider}/{@code Provider}/{@code Optional}/…)
   *       derive from the type argument, with an optional {@code Provider} suffix ({@code
   *       ObjectProvider&lt;X&gt; → xProvider});
   *   <li>{@code I}-prefixed interfaces drop the {@code I} ({@code ILogErrorMapper →
   *       logErrorMapper}).
   * </ul>
   *
   * <p>Skipped: statics, non-private, non-final, primitives, {@code java.*} types,
   * constant-style names, records, enums. Same-type multiples keep a distinguishing name
   * rooted at the type (e.g. {@code readRepository}/{@code writeRepository}) — add them to
   * the call-site allowlist instead of renaming.
   */
  public static void checkInjectionNaming(JavaClasses classes) {
    classes().that(beanClasses()).should(namingMatchesType()).allowEmptyShould(true).check(classes);
  }

  /**
   * Only Spring-managed beans carry constructor injection; model/data carriers (records,
   * entities, {@code *Pb} projections) keep their domain field names.
   */
  private static DescribedPredicate<JavaClass> beanClasses() {
    return new DescribedPredicate<JavaClass>("be Spring beans") {
      @Override
      public boolean test(JavaClass javaClass) {
        return javaClass.isAnnotatedWith(Component.class)
            || javaClass.isAnnotatedWith(Service.class)
            || javaClass.isAnnotatedWith(Controller.class)
            || javaClass.isAnnotatedWith(RestController.class)
            || javaClass.isAnnotatedWith(Repository.class)
            || javaClass.isAnnotatedWith(Configuration.class);
      }
    };
  }

  private static ArchCondition<JavaClass> namingMatchesType() {
    return new ArchCondition<JavaClass>("have injection fields named after their type") {
      @Override
      public void check(JavaClass clazz, ConditionEvents events) {
        if (clazz.isRecord() || clazz.isEnum() || clazz.isInterface() || clazz.isAnnotation()) {
          return;
        }
        for (JavaField field : clazz.getFields()) {
          if (!isPrivateFinal(field)) {
            continue;
          }
          JavaClass rawType = field.getRawType();
          if (rawType.isPrimitive() || rawType.getPackageName().startsWith("java.")) {
            continue;
          }
          String name = field.getName();
          if (name.matches("[A-Z0-9_]+")) {
            continue;
          }
          if (matchesExpectedName(field, name)) {
            continue;
          }
          events.add(
              SimpleConditionEvent.violated(
                  field,
                  field.getFullName()
                      + " should be named "
                      + expectedInjectionNames(field)
                      + " (type name decapitalized; see spring/service.md §依赖注入)"));
        }
      }
    };
  }

  private static boolean isPrivateFinal(JavaField field) {
    return !field.getModifiers().contains(JavaModifier.STATIC)
        && field.getModifiers().contains(JavaModifier.PRIVATE)
        && field.getModifiers().contains(JavaModifier.FINAL);
  }

  private static boolean matchesExpectedName(JavaField field, String name) {
    if (SHORT_INJECTION_NAMES.contains(name)
        && shortNameMatches(field.getRawType(), name)) {
      return true;
    }
    return expectedInjectionNames(field).contains(name);
  }

  private static final Set<String> SHORT_INJECTION_NAMES = Set.of("dsl", "recordMapper", "mapper");

  private static boolean shortNameMatches(JavaClass rawType, String name) {
    if (name.equals("dsl")) {
      return rawType.getSimpleName().equals("DSLContext");
    }
    if (name.equals("recordMapper")) {
      return rawType.getSimpleName().endsWith("RecordMapper");
    }
    // Domain MapStruct mappers (unique per bean): XxxMapper → mapper.
    // *RecordMapper keeps the more specific recordMapper short name above.
    return name.equals("mapper")
        && rawType.getSimpleName().endsWith("Mapper")
        && !rawType.getSimpleName().endsWith("RecordMapper");
  }

  private static List<String> expectedInjectionNames(JavaField field) {
    JavaClass rawType = field.getRawType();
    String simple = rawType.getSimpleName();
    if (WRAPPER_TYPES.contains(simple) && field.getType() instanceof JavaParameterizedType parameterized) {
      List<JavaType> args = parameterized.getActualTypeArguments();
      if (!args.isEmpty() && args.get(0) instanceof JavaClass arg) {
        String base = decapitalize(stripInterfacePrefix(arg.getSimpleName()));
        return List.of(base, base + "Provider");
      }
    }
    return List.of(decapitalize(stripInterfacePrefix(simple)));
  }

  private static final Set<String> WRAPPER_TYPES =
      Set.of("ObjectProvider", "ObjectFactory", "Provider", "Supplier", "Optional");

  private static String stripInterfacePrefix(String simpleName) {
    if (simpleName.length() > 1
        && simpleName.charAt(0) == 'I'
        && Character.isUpperCase(simpleName.charAt(1))) {
      return simpleName.substring(1);
    }
    return simpleName;
  }

  /**
   * Type-name → injection field: leading acronym run lowercased ({@code CBORMapper} → {@code
   * cborMapper}, {@code URLParser} → {@code urlParser}); single leading capital stays simple
   * ({@code UserService} → {@code userService}).
   */
  private static String decapitalize(String name) {
    if (name == null || name.isEmpty()) {
      return name;
    }
    int upperRun = 0;
    while (upperRun < name.length() && Character.isUpperCase(name.charAt(upperRun))) {
      upperRun++;
    }
    if (upperRun <= 1) {
      return name.substring(0, 1).toLowerCase(Locale.ROOT) + name.substring(1);
    }
    if (upperRun == name.length()) {
      return name.toLowerCase(Locale.ROOT);
    }
    return name.substring(0, upperRun - 1).toLowerCase(Locale.ROOT) + name.substring(upperRun - 1);
  }

  /** Mirrors {@code resideInAnyPackage} for {@code "..a.b.."}-style patterns. */
  private static boolean matchesAnyPackage(String packageName, String... patterns) {
    for (String pattern : patterns) {
      String needle = pattern;
      while (needle.startsWith(".")) {
        needle = needle.substring(1);
      }
      while (needle.endsWith(".")) {
        needle = needle.substring(0, needle.length() - 1);
      }
      if (!needle.isEmpty() && packageName.contains(needle)) {
        return true;
      }
    }
    return false;
  }

  private static String moduleRootOf(String packageName, String domainRootPackage) {
    String prefix = domainRootPackage + ".";
    if (!packageName.startsWith(prefix)) {
      return null;
    }
    int nextDot = packageName.indexOf('.', prefix.length());
    return nextDot < 0 ? prefix : packageName.substring(0, nextDot + 1);
  }

  /** Repositories persist: located in {@code repository/}, no deps on handlers/controllers. */
  public static void checkRepositoriesLocatedAndFree(JavaClasses classes) {
    classes()
        .that()
        .areAnnotatedWith(Repository.class)
        .should()
        .resideInAPackage("..repository..")
        .allowEmptyShould(true).check(classes);
    noClasses()
        .that()
        .resideInAPackage("..repository..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..handler..")
        .allowEmptyShould(true).check(classes);
    noClasses()
        .that()
        .resideInAPackage("..repository..")
        .should()
        .dependOnClassesThat()
        .areAnnotatedWith(RestController.class)
        .allowEmptyShould(true).check(classes);
  }

  /**
   * Use-case handlers live in {@code handler/}.
   *
   * @param domainPatterns ArchUnit package patterns scoping the domain (e.g. {@code
   *     "..system.dict.."}); SPI interfaces stay excluded via {@code areNotInterfaces}
   *     (mirrors {@code spring-http.sh} non-layering exclusions)
   */
  public static void checkHandlersLocated(JavaClasses classes, String... domainPatterns) {
    classes()
        .that()
        .haveSimpleNameEndingWith("Handler")
        .and()
        .areNotInterfaces()
        .and()
        .resideInAnyPackage(domainPatterns)
        .should()
        .resideInAPackage("..handler..")
        .allowEmptyShould(true).check(classes);
  }

  /** Handlers are {@code @Component} use-case beans. */
  public static void checkHandlersAsComponents(JavaClasses classes) {
    classes()
        .that()
        .resideInAPackage("..handler..")
        .and()
        .haveSimpleNameEndingWith("Handler")
        .should()
        .beAnnotatedWith(Component.class)
        .allowEmptyShould(true).check(classes);
  }

  /** Handlers are never {@code @Service}. */
  public static void checkHandlersNotAsServices(JavaClasses classes) {
    noClasses()
        .that()
        .resideInAPackage("..handler..")
        .should()
        .beAnnotatedWith(Service.class)
        .allowEmptyShould(true).check(classes);
  }

  /**
   * Servlet-stack handlers own their transaction on the {@code handle()} entry point (method
   * level only: a class-level annotation would also cover future {@code public} helpers, so only
   * {@code handle()} is checked, {@code readOnly=true} included). A module can host several faces
   * (e.g. servlet {@code admin/} next to cache-backed {@code game/}), so the check takes the face
   * patterns instead of matching every {@code ..handler..} package.
   */
  public static void checkHandlersTransactional(JavaClasses classes, String... domainPatterns) {
    checkHandlersTransactional(classes, Set.of(), domainPatterns);
  }

  /**
   * Same as {@link #checkHandlersTransactional(JavaClasses, String...)}, except handlers whose
   * {@code handle()} only touches Redis/Caffeine/object storage and deliberately avoids opening a
   * JDBC connection. Pass their fully-qualified class names; everything else still needs the
   * transaction annotation.
   */
  public static void checkHandlersTransactional(
      JavaClasses classes, Set<String> cacheOnlyHandlers, String... domainPatterns) {
    methods()
        .that()
        .areDeclaredInClassesThat(
            JavaClass.Predicates.resideInAnyPackage(
                    domainPatterns.length == 0 ? new String[] {"..handler.."} : domainPatterns)
                .and(JavaClass.Predicates.simpleNameEndingWith("Handler"))
                .and(
                    DescribedPredicate.describe(
                        "not a cache-only handler",
                        clazz -> !cacheOnlyHandlers.contains(clazz.getName()))))
        .and()
        .haveName("handle")
        .should()
        .beAnnotatedWith(Transactional.class)
        .allowEmptyShould(true).check(classes);
  }

  /** Business time goes through a testable clock, never {@code System.currentTimeMillis}. */
  public static void checkNoSystemCurrentTimeMillis(JavaClasses classes) {
    noClasses().should().callMethod(System.class, "currentTimeMillis").allowEmptyShould(true).check(classes);
  }

  /**
   * Scoped variant: hosts with legacy exclusions (e.g. game/ pending migration) pass a scope
   * predicate instead of inlining the rule. The predicate selects the classes the rule applies
   * to; classes outside the scope are not checked.
   */
  public static void checkNoSystemCurrentTimeMillis(
      JavaClasses classes, DescribedPredicate<JavaClass> scope) {
    noClasses()
        .should()
        .callMethod(System.class, "currentTimeMillis")
        .allowEmptyShould(true)
        .check(classes.that(scope));
  }

  /**
   * Enum concepts travel as generated enum types, never {@code String} fields (see {@code
   * core/enum-strategy.md} §枚举值禁字符串透传): a String field named {@code kind} /
   * {@code shareType*} / {@code quality*} is an untyped enum carriage (literal typos are
   * silently false, {@code valueOf} throws at runtime). Jackson polymorphic {@code type}
   * discriminators and {@code ErrorMessage} codes keep their String names and are out of
   * scope (different field names).
   */
  public static void checkNoStringEnumFields(JavaClasses classes) {
    checkNoStringEnumFields(classes, Set.of());
  }

  /**
   * Same as {@link #checkNoStringEnumFields(JavaClasses)}, except for explicitly documented open
   * vocabularies: entries are {@code com.example.Foo#bar} field coordinates whose String carriage
   * is verified intentional (generative step kinds, reporter-defined alert kinds — closing them
   * would reject legitimate input, not just typos). Entries that no longer match a String field
   * are inert (a typo'd entry fails safe: the violation is still reported).
   */
  public static void checkNoStringEnumFields(JavaClasses classes, Set<String> allowedFields) {
    classes().should(noStringEnumFields(allowedFields)).allowEmptyShould(true).check(classes);
  }

  private static ArchCondition<JavaClass> noStringEnumFields(Set<String> allowedFields) {
    return new ArchCondition<JavaClass>("not carry enum values as String fields") {
      @Override
      public void check(JavaClass clazz, ConditionEvents events) {
        for (JavaField field : clazz.getFields()) {
          if (field.getModifiers().contains(JavaModifier.STATIC)) {
            continue;
          }
          if (!field.getRawType().getFullName().equals(String.class.getName())) {
            continue;
          }
          if (!field.getName().matches("(kind|shareType.*|quality|qualities)")) {
            continue;
          }
          if (allowedFields.contains(clazz.getFullName() + "#" + field.getName())) {
            continue;
          }
          events.add(
              SimpleConditionEvent.violated(
                  field,
                  field.getFullName()
                      + " carries an enum concept as String (use the generated enum type;"
                      + " see core/enum-strategy.md §枚举值禁字符串透传)"));
        }
      }
    };
  }

  /**
   * Handlers declare no HTTP surface: routes come from proto {@code m_opts} via {@code
   * @WebpbRequestMapping} on controllers (see {@code spring/service.md}).
   */
  public static void checkHandlersFreeOfHttpMappings(JavaClasses classes) {
    noClasses()
        .that()
        .resideInAPackage("..handler..")
        .should()
        .beAnnotatedWith(RestController.class)
        .allowEmptyShould(true).check(classes);
    noClasses()
        .that()
        .resideInAPackage("..handler..")
        .should()
        .beAnnotatedWith(Controller.class)
        .allowEmptyShould(true).check(classes);
    noClasses()
        .that()
        .resideInAPackage("..handler..")
        .should()
        .beAnnotatedWith(RequestMapping.class)
        .allowEmptyShould(true).check(classes);
    noClasses()
        .that()
        .resideInAPackage("..handler..")
        .should()
        .beAnnotatedWith(GetMapping.class)
        .allowEmptyShould(true).check(classes);
    noClasses()
        .that()
        .resideInAPackage("..handler..")
        .should()
        .beAnnotatedWith(PostMapping.class)
        .allowEmptyShould(true).check(classes);
    noClasses()
        .that()
        .resideInAPackage("..handler..")
        .should()
        .beAnnotatedWith(PutMapping.class)
        .allowEmptyShould(true).check(classes);
    noClasses()
        .that()
        .resideInAPackage("..handler..")
        .should()
        .beAnnotatedWith(DeleteMapping.class)
        .allowEmptyShould(true).check(classes);
    noClasses()
        .that()
        .resideInAPackage("..handler..")
        .should()
        .beAnnotatedWith(PatchMapping.class)
        .allowEmptyShould(true).check(classes);
    noMethods()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("..handler..")
        .should()
        .beAnnotatedWith(RequestMapping.class)
        .allowEmptyShould(true).check(classes);
    noMethods()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("..handler..")
        .should()
        .beAnnotatedWith(GetMapping.class)
        .allowEmptyShould(true).check(classes);
    noMethods()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("..handler..")
        .should()
        .beAnnotatedWith(PostMapping.class)
        .allowEmptyShould(true).check(classes);
    noMethods()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("..handler..")
        .should()
        .beAnnotatedWith(PutMapping.class)
        .allowEmptyShould(true).check(classes);
    noMethods()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("..handler..")
        .should()
        .beAnnotatedWith(DeleteMapping.class)
        .allowEmptyShould(true).check(classes);
    noMethods()
        .that()
        .areDeclaredInClassesThat()
        .resideInAPackage("..handler..")
        .should()
        .beAnnotatedWith(PatchMapping.class)
        .allowEmptyShould(true).check(classes);
  }

  /** Controllers are endpoints, never buried in {@code internal/} packages. */
  public static void checkControllersOutOfInternal(JavaClasses classes) {
    noClasses()
        .that()
        .areAnnotatedWith(RestController.class)
        .should()
        .resideInAPackage("..internal..")
        .allowEmptyShould(true).check(classes);
  }

  /**
   * Domain roots keep only {@code Controller / Mapper / Service}: no fourth-kind files
   * ({@code *Rules / *Rows / *Tables / *Registry / *Configuration}) directly under a domain
   * root, and no {@code @Configuration} classes at domain root (see {@code
   * spring/service.md} MUST + {@code system-module-layout.flow.md}).
   *
   * <p>A domain root is exactly one segment below {@code domainRootPackage} (e.g. {@code
   * ..system.support} below {@code io.github.jinganix.guess.vendor.kit.system}); subpackages like {@code
   * handler/}, {@code model/}, {@code repository/} are out of scope. Infra/SPI-only packages
   * without a {@code *Controller} are still checked — fourth-kind names never belong at root.
   *
   * @param domainRootPackages the module roots (e.g. {@code io.github.jinganix.guess.vendor.kit.system})
   */
  public static void checkDomainRootFreeOfFourthKind(
      JavaClasses classes, String... domainRootPackages) {
    DescribedPredicate<JavaClass> underRoots =
        new DescribedPredicate<JavaClass>("reside under domain roots") {
          @Override
          public boolean test(JavaClass javaClass) {
            String pkg = javaClass.getPackageName();
            for (String root : domainRootPackages) {
              if (pkg.equals(root) || pkg.startsWith(root + ".")) {
                return true;
              }
            }
            return false;
          }
        };
    classes()
        .that(underRoots)
        .should(domainRootWithoutFourthKind(domainRootPackages))
        .allowEmptyShould(true).check(classes);
  }

  private static ArchCondition<JavaClass> domainRootWithoutFourthKind(String[] roots) {
    return new ArchCondition<JavaClass>("be free of fourth-kind files at domain root") {
      @Override
      public void check(JavaClass clazz, ConditionEvents events) {
        String pkg = clazz.getPackageName();
        if (!isDomainRoot(pkg, roots)) {
          return;
        }
        if (clazz.isInterface() || clazz.isEnum() || clazz.isAnnotation()) {
          return;
        }
        String simple = clazz.getSimpleName();
        if (simple.equals("package-info")) {
          return;
        }
        boolean fourthKind =
            simple.endsWith("Rules")
                || simple.endsWith("Rows")
                || simple.endsWith("Tables")
                || simple.endsWith("Registry")
                || simple.endsWith("Configuration");
        boolean rootConfig = clazz.isAnnotatedWith(Configuration.class);
        if (fourthKind || rootConfig) {
          events.add(
              SimpleConditionEvent.violated(
                  clazz,
                  clazz.getFullName()
                      + " must not live at domain root "
                      + pkg
                      + " (move to handler/model/repository or feature subpackage;"
                      + " see spring/service.md MUST)"));
        }
      }
    };
  }

  private static boolean isDomainRoot(String packageName, String[] roots) {
    for (String root : roots) {
      String prefix = root + ".";
      if (!packageName.startsWith(prefix)) {
        continue;
      }
      String remainder = packageName.substring(prefix.length());
      if (!remainder.isEmpty() && !remainder.contains(".")) {
        return true;
      }
    }
    return false;
  }

  /**
   * Domain code never touches untestable platform calls (see {@code spring/service.md} MUST):
   *
   * <ul>
   *   <li>{@code System.exit} / {@code Runtime.exit} — no process control in domain;
   *   <li>{@code Thread.sleep} — blocking sleeps belong in helper infra (retry/backoff), never
   *       in domain orchestration;
   *   <li>{@code new Date()} (no-arg) — system-clock read; use {@code UtilsService} clock or
   *       {@code new Date(long)} for external-API compat.
   * </ul>
   *
   * <p>{@code System.out/err} and {@code System.currentTimeMillis} are covered by the fast
   * {@code forbidden-calls} conformance grep (field access) and {@link
   * #checkNoSystemCurrentTimeMillis} respectively.
   *
   * @param domainPatterns ArchUnit package patterns scoping the domain under test
   */
  public static void checkNoForbiddenPlatformCalls(
      JavaClasses classes, String... domainPatterns) {
    noClasses()
        .that()
        .resideInAnyPackage(domainPatterns)
        .should()
        .callMethod(System.class, "exit", int.class)
        .allowEmptyShould(true).check(classes);
    noClasses()
        .that()
        .resideInAnyPackage(domainPatterns)
        .should()
        .callMethod(Runtime.class, "exit", int.class)
        .allowEmptyShould(true).check(classes);
    noClasses()
        .that()
        .resideInAnyPackage(domainPatterns)
        .should()
        .callMethod(Thread.class, "sleep", long.class)
        .allowEmptyShould(true).check(classes);
    noClasses()
        .that()
        .resideInAnyPackage(domainPatterns)
        .should()
        .callConstructor(java.util.Date.class)
        .allowEmptyShould(true).check(classes);
  }

  /**
   * Pb/DTO mapping goes through {@code {Domain}Mapper} (see {@code spring/service.md}
   * §转换归属): {@code handler/} classes and {@code *Service} beans never call {@code *Pb.set*}
   * on {@code *Pb}-suffixed types.
   *
   * <p>Out of scope (allowed): empty Pb ({@code new FooPb()} with no setters), {@code
   * *Request} / {@code *Response} envelopes (e.g. {@code new FooRetrieveResponse(pb)}),
   * {@code *Mapper} classes themselves, and projection-only helpers outside handler/service
   * packages. Hosts keep stock allowlists local and wire this check in their own {@code
   * *ArchitectureTest} once the domain is refactored.
   */
  public static void checkPbMappingViaMapper(JavaClasses classes) {
    classes()
        .that(
            JavaClass.Predicates.resideInAPackage("..handler..")
                .or(serviceClasses()))
        .should(pbMappingViaMapper())
        .allowEmptyShould(true).check(classes);
  }

  /**
   * Entity filling goes through {@code Mapper#fill(@MappingTarget ...)} (see {@code
   * spring/service.md} §转换归属): {@code handler/} classes and {@code *Service} beans never
   * call {@code Entity.set*} field-by-field for business backfill.
   *
   * <p>Construction via {@code new Entity()} stays allowed (id/tenant/timestamp wiring lives
   * with the caller); only {@code set*} calls on {@code ..model..} owners are flagged.
   * Hosts keep stock allowlists local and wire this check once the domain is refactored.
   */
  public static void checkEntityFillingViaMapper(JavaClasses classes) {
    classes()
        .that(
            JavaClass.Predicates.resideInAPackage("..handler..")
                .or(serviceClasses()))
        .should(entityFillingViaMapper())
        .allowEmptyShould(true).check(classes);
  }

  private static boolean isBusinessPb(JavaClass target) {
    return isInPackageSegment(target.getPackageName(), ".proto.")
        && target.getSimpleName().endsWith("Pb")
        && !target.getSimpleName().endsWith("Request")
        && !target.getSimpleName().endsWith("Response");
  }

  private static boolean isModelOwner(JavaClass target) {
    return isInPackageSegment(target.getPackageName(), ".model.");
  }

  /** Matches a package segment (also when it is the last segment, e.g. {@code ..resident.model}). */
  private static boolean isInPackageSegment(String packageName, String dottedSegment) {
    String segment = dottedSegment.substring(1, dottedSegment.length() - 1);
    return (".." + packageName + ".").contains("." + segment + ".");
  }

  private static ArchCondition<JavaClass> pbMappingViaMapper() {
    return new ArchCondition<JavaClass>("map Pb via Mapper") {
      @Override
      public void check(JavaClass clazz, ConditionEvents events) {
        for (JavaMethodCall call : clazz.getMethodCallsFromSelf()) {
          JavaClass target = call.getTargetOwner();
          if (isBusinessPb(target) && call.getName().startsWith("set")) {
            events.add(
                SimpleConditionEvent.violated(
                    clazz,
                    clazz.getFullName()
                        + " calls "
                        + target.getSimpleName()
                        + "."
                        + call.getName()
                        + " (map via {Domain}Mapper#toPb; see spring/service.md §转换归属)"));
          }
        }
      }
    };
  }

  private static ArchCondition<JavaClass> entityFillingViaMapper() {
    return new ArchCondition<JavaClass>("fill entities via Mapper") {
      @Override
      public void check(JavaClass clazz, ConditionEvents events) {
        Set<JavaCodeUnit> creationOrigins = new HashSet<>();
        for (JavaConstructorCall call : clazz.getConstructorCallsFromSelf()) {
          if (isModelOwner(call.getTargetOwner())) {
            creationOrigins.add(call.getOrigin());
          }
        }
        for (JavaMethodCall call : clazz.getMethodCallsFromSelf()) {
          JavaClass target = call.getTargetOwner();
          if (!isModelOwner(target) || !call.getName().startsWith("set")) {
            continue;
          }
          if (creationOrigins.contains(call.getOrigin())) {
            continue;
          }
          events.add(
              SimpleConditionEvent.violated(
                  clazz,
                  clazz.getFullName()
                      + " calls "
                      + target.getSimpleName()
                      + "."
                      + call.getName()
                      + " (fill via Mapper#fill(@MappingTarget ...);"
                      + " see spring/service.md §转换归属)"));
        }
      }
    };
  }

  /**
   * Mappers prefer MapStruct auto-mapping (see {@code spring/service.md} §转换归属): a concrete
   * method in a {@code *Mapper} that calls {@code *Pb.set*} must also call an abstract mapping
   * method declared in the same class (i.e. bulk mapping is generated, the concrete body only
   * backfills ignored fields). Fully hand-written whole-object mapping is forbidden.
   *
   * <p>Out of scope (allowed): empty Pb construction (no setters), setters on non-{@code *Pb}
   * types, and generated {@code *MapperImpl} classes (matched by exact {@code *Mapper} suffix
   * only). Hosts wire this check in their own {@code *ArchitectureTest} once mappers are
   * refactored.
   */
  public static void checkMappersPreferAutoMapping(JavaClasses classes) {
    classes()
        .that()
        .haveSimpleNameEndingWith("Mapper")
        .should(preferAutoMapping())
        .allowEmptyShould(true).check(classes);
  }

  private static ArchCondition<JavaClass> preferAutoMapping() {
    return new ArchCondition<JavaClass>("prefer MapStruct auto-mapping in Mappers") {
      @Override
      public void check(JavaClass clazz, ConditionEvents events) {
        Set<String> abstractMethodNames = new HashSet<>();
        for (JavaMethod method : clazz.getMethods()) {
          if (method.getModifiers().contains(JavaModifier.ABSTRACT)) {
            abstractMethodNames.add(method.getName());
          }
        }
        for (JavaMethod method : clazz.getMethods()) {
          if (method.getName().startsWith("<")) {
            continue;
          }
          boolean setsPb = false;
          boolean delegatesToAbstract = false;
          for (JavaMethodCall call : method.getMethodCallsFromSelf()) {
            if (isBusinessPb(call.getTargetOwner()) && call.getName().startsWith("set")) {
              setsPb = true;
            }
            if (call.getTargetOwner().getFullName().equals(clazz.getFullName())
                && abstractMethodNames.contains(call.getName())) {
              delegatesToAbstract = true;
            }
          }
          if (setsPb && !delegatesToAbstract) {
            events.add(
                SimpleConditionEvent.violated(
                    method,
                    clazz.getSimpleName()
                        + "."
                        + method.getName()
                        + " hand-maps Pb without delegating to an abstract MapStruct method"
                        + " (declare an abstract toPb/fill/page and backfill ignored fields only;"
                        + " see spring/service.md §转换归属)"));
          }
        }
      }
    };
  }

  /**
   * Servlet-stack host one-call wiring (see {@code setup.md} §4.1): wires the full stable set
   * in a single call so new MUSTs land in hosts without per-rule edits. Faces are the host's
   * top-level domains (e.g. {@code "admin", "system"}); package patterns ({@code ..admin..})
   * and domain roots ({@code <hostRoot>.admin}) derive from them, so patterns and roots
   * cannot drift apart.
   *
   * <p>Out of scope (stays host-local): {@code
   * checkServiceImplsOnlyDependOnSameModuleHandlers} is the game-module variant of {@code
   * checkServiceImplsFreeOfHandlerAndController} (see the {@code arch-guard-wiring} gate) and
   * host-only checks (dedicated authority/operation codes, modulith alignment). There are no
   * per-host exceptions: a host-specific layout that
   * conflicts with a rule (e.g. a handler outside {@code handler/}) is refactored to
   * comply, never pinned. Progressive mapper rules live in {@link #checkMapperRules} and
   * wire separately.
   *
   * <p>Machine completeness: the {@code arch-guard-wiring} conformance gate parses this
   * method's body as the stable set — adding a rule here automatically requires it in
   * every host, with no pins allowed.
   */
  public static void checkHostDefaults(JavaClasses classes, String hostRootPackage, String... faces) {
    checkHostDefaults(classes, hostRootPackage, Set.of(), faces);
  }

  /**
   * Same as {@link #checkHostDefaults(JavaClasses, String, String[])}, with documented open
   * string-enum vocabularies exempted (see {@link #checkNoStringEnumFields(JavaClasses, Set)}).
   * The exemption list itself is part of the host's architecture decision, not a pin.
   */
  public static void checkHostDefaults(
      JavaClasses classes, String hostRootPackage, Set<String> allowedStringEnums, String... faces) {
    String[] patterns = new String[faces.length];
    String[] roots = new String[faces.length];
    for (int i = 0; i < faces.length; i++) {
      patterns[i] = ".." + faces[i] + "..";
      roots[i] = hostRootPackage + "." + faces[i];
    }
    checkControllersThin(classes);
    checkHandlersFreeOfControllers(classes);
    checkServicesFreeOfHandlerAndController(classes);
    checkServiceImplsFreeOfHandlerAndController(classes);
    checkServicesFreeOfProtoEnvelopes(classes, patterns);
    checkRepositoriesLocatedAndFree(classes);
    checkHandlersLocated(classes, patterns);
    checkHandlersAsComponents(classes);
    checkHandlersNotAsServices(classes);
    checkHandlersTransactional(classes);
    checkHandlersFreeOfHttpMappings(classes);
    checkControllersOutOfInternal(classes);
    checkHandlersOwnUseCaseLogic(classes);
    checkNoConfigPackage(classes, patterns);
    checkServiceMigrationsLiveInMigrationsPackage(classes);
    checkDomainRootFreeOfFourthKind(classes, roots);
    checkInjectionNaming(classes);
    checkNoSystemCurrentTimeMillis(classes);
    checkNoStringEnumFields(classes, allowedStringEnums);
    checkNoForbiddenPlatformCalls(classes, patterns);
  }

  /**
   * Progressive mapper wiring (see {@code spring/service.md} §转换归属): {@code *Pb.set*} /
   * entity filling / hand-written whole-object mappers. Progressive means wire-as-you-refactor:
   * hosts adopt this call as domains are migrated, and the {@code arch-guard-wiring} gate treats
   * the rules in it as opt-in rather than mandatory (unlike {@link #checkHostDefaults}). Legacy
   * call sites are refactored into {@code Mapper} methods, never pinned.
   */
  public static void checkMapperRules(JavaClasses classes) {
    checkPbMappingViaMapper(classes);
    checkEntityFillingViaMapper(classes);
    checkMappersPreferAutoMapping(classes);
  }

  /**
   * Use-case handlers own their use case: a Handler that injects exactly one service bean
   * and delegates to a service method no one else calls is a pure pass-through shell —
   * the service method's logic belongs in the Handler's {@code handle()}. Extract to
   * Service only when 2+ call sites share the logic (see {@code spring/service.md} §分层).
   *
   * <p>Skipped: handlers with 2+ injected beans (they assemble/map/assert in {@code
   * handle()}, the service call is one step of a real orchestration); injected types that
   * are interfaces or live in a public {@code api} package (a module-boundary contract
   * whose callers may live outside this scan); service methods
   * with 0 callers (dead or out-of-scope callers) or 2+ callers (genuinely shared);
   * and calls whose target owner is the service class itself (internal reuse).
   */
  public static void checkHandlersOwnUseCaseLogic(JavaClasses classes) {
    classes()
        .that()
        .resideInAPackage("..handler..")
        .and()
        .haveSimpleNameEndingWith("Handler")
        .and()
        .areNotInterfaces()
        .should(ownUseCaseLogic(classes))
        .allowEmptyShould(true)
        .check(classes);
  }

  private static ArchCondition<JavaClass> ownUseCaseLogic(JavaClasses classes) {
    return new ArchCondition<JavaClass>("own their use-case logic instead of pass-through") {
      @Override
      public void check(JavaClass handler, ConditionEvents events) {
        List<JavaField> beanFields = new ArrayList<>();
        for (JavaField field : handler.getFields()) {
          if (field.getModifiers().contains(JavaModifier.STATIC)) {
            continue;
          }
          if (field.getRawType().isPrimitive()) {
            continue;
          }
          if (field.getRawType().getPackageName().startsWith("java.")) {
            continue;
          }
          beanFields.add(field);
        }
        if (beanFields.size() != 1) {
          return;
        }
        JavaClass injectedType = beanFields.get(0).getRawType();
        if (injectedType.isInterface() || injectedType.getPackageName().endsWith(".api")) {
          return;
        }
        String serviceTypeName = injectedType.getSimpleName();
        if (!serviceTypeName.endsWith("Service") && !serviceTypeName.endsWith("ServiceImpl")) {
          return;
        }
        String serviceName = injectedType.getFullName();
        // A service class used by 2+ callers is a genuinely shared Service: its cohesive method
        // set justifies extraction even when an individual method happens to have one caller.
        if (externalCallerClassCount(classes, serviceName) > 1) {
          return;
        }
        for (JavaMethod handle : handler.getMethods()) {
          if (!handle.getName().equals("handle")) {
            continue;
          }
          for (JavaMethodCall call : handle.getMethodCallsFromSelf()) {
            if (!call.getTargetOwner().getFullName().equals(serviceName)) {
              continue;
            }
            String targetName = call.getTarget().getName();
            List<String> targetParams = paramNames(call);
            if (!onlyExternalCallSite(
                classes, serviceName, targetName, targetParams, handler.getFullName())) {
              continue;
            }
            if (internalReuseCount(classes, serviceName, targetName, targetParams) > 0) {
              continue;
            }
            if (!delegatesWholeUseCase(handle, serviceName)) {
              continue;
            }
            {
              events.add(
                  SimpleConditionEvent.violated(
                      handle,
                      handler.getSimpleName()
                          + ".handle() delegates its whole use case to single-use "
                          + call.getTargetOwner().getSimpleName()
                          + "."
                          + targetName
                          + " (sole caller; handler injects nothing else)"
                          + " (fold the logic into the Handler; extract to Service only"
                          + " when 2+ call sites share it; see spring/service.md §分层)"));
            }
          }
        }
      }
    };
  }

  private static List<String> paramNames(JavaMethodCall call) {
    List<String> params = new ArrayList<>();
    for (JavaType param : call.getTarget().getRawParameterTypes()) {
      params.add(param.getName());
    }
    return params;
  }

  /**
   * Wholesale delegation means {@code handle()} adds no work of its own: its only self-originated
   * calls go to the service (guards/asserts on called types, constructors of proto responses and
   * the handler's own helpers are bookkeeping). Anything else (repository/other bean calls, value
   * computation) makes the handler a real orchestrator, not a pass-through shell.
   */
  private static boolean delegatesWholeUseCase(JavaMethod handle, String serviceName) {
    JavaClass owner = handle.getOwner();
    for (JavaMethodCall call : handle.getMethodCallsFromSelf()) {
      JavaClass target = call.getTargetOwner();
      String targetName = target.getFullName();
      if (targetName.equals(owner.getFullName()) || targetName.equals(serviceName)) {
        continue;
      }
      if (isBookkeepingTarget(target, call.getName())) {
        continue;
      }
      return false;
    }
    return true;
  }

  /**
   * Bookkeeping a shell still performs: reading the proto request, pulling the tenant/user id,
   * and constructing the proto response. Everything else (repository or helper calls, collection
   * and string work on service results) means {@code handle()} adds logic of its own.
   */
  private static boolean isBookkeepingTarget(JavaClass target, String methodName) {
    String pkg = target.getPackageName();
    if (pkg.startsWith("io.github.jinganix.guess.vendor.kit.helper.tenant")
        || pkg.startsWith("io.github.jinganix.guess.vendor.kit.helper.auth")) {
      return true;
    }
    String name = target.getSimpleName();
    if (name.endsWith("Request") || name.endsWith("Response")) {
      return true;
    }
    if (name.endsWith("Pb")) {
      return methodName.startsWith("set") || methodName.startsWith("get") || methodName.equals("<init>");
    }
    return false;
  }

  /** Distinct classes (outside the service itself) that call any method of the service. */
  private static int externalCallerClassCount(JavaClasses classes, String owner) {
    Set<String> callers = new HashSet<>();
    for (JavaClass clazz : classes) {
      if (clazz.getFullName().equals(owner)) {
        continue;
      }
      for (JavaMethod method : clazz.getMethods()) {
        for (JavaMethodCall call : method.getMethodCallsFromSelf()) {
          if (call.getTargetOwner().getFullName().equals(owner)) {
            callers.add(clazz.getFullName());
          }
        }
      }
    }
    return callers.size();
  }

  /** True when every call site of the method lives in the calling handler (no other caller). */
  private static boolean onlyExternalCallSite(
      JavaClasses classes, String owner, String name, List<String> params, String callerClass) {
    for (JavaClass clazz : classes) {
      for (JavaMethod method : clazz.getMethods()) {
        for (JavaMethodCall call : method.getMethodCallsFromSelf()) {
          if (!call.getTargetOwner().getFullName().equals(owner)
              || !call.getTarget().getName().equals(name)
              || !paramNames(call).equals(params)) {
            continue;
          }
          if (!method.getOwner().getFullName().equals(callerClass)) {
            return false;
          }
        }
      }
    }
    return true;
  }

  /** Calls from the service class itself: the method is internal reuse, not a single-use target. */
  private static int internalReuseCount(
      JavaClasses classes, String owner, String name, List<String> params) {
    int count = 0;
    for (JavaClass clazz : classes) {
      if (!clazz.getFullName().equals(owner)) {
        continue;
      }
      for (JavaMethod method : clazz.getMethods()) {
        for (JavaMethodCall call : method.getMethodCallsFromSelf()) {
          if (call.getTargetOwner().getFullName().equals(owner)
              && call.getTarget().getName().equals(name)
              && paramNames(call).equals(params)) {
            count++;
          }
        }
      }
    }
    return count;
  }

  /**
   * Migration scripts live in {@code migrations/} (see {@code spring/atlas.md}
   * §与 ServiceMigration 的配对): transitional scripts follow the {@code Migrate*} naming
   * convention and must reside in a {@code ..migrate..} / {@code ..migrations..} / {@code
   * ..migration..} package — the framework ({@code migrate/}) or a host one-off ({@code
   * migrations/}). Business packages ({@code admin/*} etc.) must never carry migration logic;
   * transitional DDL + data backfill ship as one startup-time script.
   */
  public static void checkServiceMigrationsLiveInMigrationsPackage(JavaClasses classes) {
    classes()
        .that()
        .haveSimpleNameStartingWith("Migrate")
        .should()
        .resideInAnyPackage("..migrate..", "..migrations..", "..migration..")
        .allowEmptyShould(true).check(classes);
  }

  /**
   * Domain code wires via {@code setup/}, never a top-level {@code config/} package.
   *
   * @param domainPatterns ArchUnit package patterns scoping the domain under test
   */
  public static void checkNoConfigPackage(JavaClasses classes, String... domainPatterns) {
    noClasses()
        .that()
        .resideInAnyPackage(domainPatterns)
        .should()
        .resideInAPackage("..config..")
        .allowEmptyShould(true).check(classes);
  }
}
