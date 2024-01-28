import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.remove
import utils.Vers.versionProtobuf
import utils.Vers.versionWebpb

plugins {
  `maven-publish`
  application
  id("com.google.protobuf")
  id("io.spring.dependency-management")
  id("java.common")
  id("org.springframework.boot")
  java
  signing
}

dependencies {
}

tasks.withType<JavaCompile> {
  options.encoding = "UTF-8"
  options.compilerArgs.addAll(
    arrayOf(
      "-Xlint:deprecation",
      "-Xlint:unchecked",
      "-Amapstruct.unmappedTargetPolicy=IGNORE"
    )
  )
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
      it.doFirst {
        delete(it.outputBaseDir)
      }
      it.builtins {
        remove("java")
      }
      it.plugins {
        id("webpb")
      }
    }
  }
}
