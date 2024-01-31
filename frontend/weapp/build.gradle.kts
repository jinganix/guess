import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.remove
import org.apache.tools.ant.taskdefs.condition.Os
import utils.Vers.versionProtobuf
import utils.Vers.versionWebpb

plugins {
  id("com.google.protobuf")
  id("conventions.common")
  java
}

val npm = if (Os.isFamily(Os.FAMILY_WINDOWS)) "npm.cmd" else "npm"

task<Exec>("npmInstall") {
  val nodeModules = file("./node_modules")
  if (nodeModules.exists()) {
    commandLine(npm, "--version")
  } else {
    commandLine(npm, "install", "--verbose")
  }
}

task<Exec>("npmStart") {
  commandLine(npm, "run", "dev")

  dependsOn("npmInstall")
}

task<Exec>("npmCheck") {
  commandLine(npm, "run", "lint")
  commandLine(npm, "run", "test")
  commandLine(npm, "run", "build")

  dependsOn("npmInstall")
}

tasks.check {
  dependsOn("npmCheck")
}

dependencies {
  implementation("io.github.jinganix.webpb:webpb-proto:${versionWebpb}")
  protobuf(project(":proto:imports"))
  protobuf(project(":proto:service"))
}

tasks.clean {
  enabled = false
}

protobuf {
  protoc {
    artifact = "com.google.protobuf:protoc:$versionProtobuf"
  }
  plugins {
    id("ts") {
      artifact = "io.github.jinganix.webpb:webpb-protoc-ts:$versionWebpb:all@jar"
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
