import com.google.protobuf.gradle.protobuf
import utils.Vers.versionAuthorizationServer
import utils.Vers.versionBcpkix
import utils.Vers.versionCaffeine
import utils.Vers.versionCommonsCodec
import utils.Vers.versionCommonsLang3
import utils.Vers.versionFlyway
import utils.Vers.versionJwt
import utils.Vers.versionMapstruct
import utils.Vers.versionJackson
import utils.Vers.versionMysqlConnector
import utils.Vers.versionNetty
import utils.Vers.versionPeashooter
import utils.Vers.versionProtobufJava
import utils.Vers.versionTestContainers
import utils.Vers.versionWebpb

plugins {
  id("java.service")
}

dependencies {
  annotationProcessor("io.github.jinganix.webpb:webpb-processor:${versionWebpb}")
  annotationProcessor("org.mapstruct:mapstruct-processor:${versionMapstruct}")
  implementation("com.auth0:java-jwt:${versionJwt}")
  implementation("com.fasterxml.jackson.core:jackson-databind:${versionJackson}")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:${versionJackson}")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${versionJackson}")
  implementation("com.github.ben-manes.caffeine:caffeine:${versionCaffeine}")
  implementation("com.google.protobuf:protobuf-java:${versionProtobufJava}")
  implementation("commons-codec:commons-codec:${versionCommonsCodec}")
  implementation("io.github.jinganix.peashooter:peashooter:${versionPeashooter}")
  implementation("io.github.jinganix.webpb:webpb-proto:${versionWebpb}")
  implementation("io.github.jinganix.webpb:webpb-runtime:${versionWebpb}")
  implementation("io.netty:netty-resolver-dns-native-macos:${versionNetty}:osx-aarch_64")
  implementation("org.apache.commons:commons-lang3:${versionCommonsLang3}")
  implementation("org.bouncycastle:bcpkix-jdk15on:${versionBcpkix}")
  implementation("org.flywaydb:flyway-core:${versionFlyway}")
  implementation("org.flywaydb:flyway-mysql:${versionFlyway}")
  implementation("org.mapstruct:mapstruct:${versionMapstruct}")
  implementation("org.springframework.boot:spring-boot-configuration-processor")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.data:spring-data-commons")
  implementation("org.springframework.security:spring-security-oauth2-authorization-server:${versionAuthorizationServer}")
  implementation("org.testcontainers:junit-jupiter:${versionTestContainers}")
  implementation("org.testcontainers:mysql:${versionTestContainers}")
  implementation("org.testcontainers:testcontainers:${versionTestContainers}")
  implementation(project(":lib:proto"))
  protobuf(project(":proto:internal"))
  protobuf(project(":proto:service"))
  runtimeOnly("mysql:mysql-connector-java:${versionMysqlConnector}")
  testAnnotationProcessor("org.mapstruct:mapstruct-processor:${versionMapstruct}")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("org.testcontainers:junit-jupiter:${versionTestContainers}")
  testImplementation("org.testcontainers:testcontainers:${versionTestContainers}")
}

tasks.withType<JavaCompile> {
  options.compilerArgs.add("--enable-preview")
}

tasks.test {
  useJUnitPlatform()
  jvmArgs("--enable-preview")
}

tasks.bootJar {
  archiveFileName.set("guess-service.jar")
  launchScript()
}
