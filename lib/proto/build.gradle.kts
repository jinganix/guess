import com.google.protobuf.gradle.*
import utils.Vers.versionJackson
import utils.Vers.versionMapstruct
import utils.Vers.versionProtobuf
import utils.Vers.versionWebpb

plugins {
  id("com.google.protobuf")
  id("io.spring.dependency-management")
  id("java.library")
  id("org.springframework.boot")
}

dependencies {
  api("io.github.jinganix.webpb:webpb-runtime:${versionWebpb}")
  compileOnly("org.mapstruct:mapstruct:${versionMapstruct}")
  implementation("com.fasterxml.jackson.core:jackson-databind:${versionJackson}")
  implementation("io.github.jinganix.webpb:webpb-proto:${versionWebpb}")
  implementation("org.springframework.boot:spring-boot-autoconfigure")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.data:spring-data-commons")
  implementation("org.springframework:spring-web")
  protobuf(project(":proto:imports"))
}

tasks.bootJar {
  enabled = false
}

tasks.jar {
  enabled = true
}

protobuf {
  protoc {
    artifact = "com.google.protobuf:protoc:${versionProtobuf}"
  }
  plugins {
    id("webpb") {
      artifact = "io.github.jinganix.webpb:webpb-protoc-java:${versionWebpb}:all@jar"
    }
  }
  generateProtoTasks {
    ofSourceSet("main").forEach {
      it.builtins {
        remove("java")
      }
      it.plugins {
        id("webpb")
      }
    }
  }
}
