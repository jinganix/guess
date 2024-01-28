import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.remove
import utils.Props
import utils.Vers
import utils.Vers.versionProtobuf
import utils.Vers.versionWebpb

plugins {
  id("com.google.protobuf")
  id("conventions.versioning")
  java
}

Props.initialize(project)
Vers.initialize(project)

repositories {
  mavenLocal()
  mavenCentral()
}

dependencies {
  implementation("io.github.jinganix.webpb:webpb-proto:${versionWebpb}")
}

protobuf {
  protoc {
    artifact = "com.google.protobuf:protoc:${versionProtobuf}"
  }
  plugins {
    id("ts") {
      artifact = "io.github.jinganix.webpb:webpb-protoc-ts:${versionWebpb}:all@jar"
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
        id("ts")
      }
    }
  }
}
