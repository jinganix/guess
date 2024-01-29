import com.google.protobuf.gradle.protobuf
import org.apache.tools.ant.taskdefs.condition.Os

plugins {
  id("ts.frontend")
}

dependencies {
  protobuf(project(":proto:service"))
  protobuf(project(":proto:imports"))
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

task<Exec>("npmCheck") {
  commandLine(npm, "run", "lint")
  commandLine(npm, "run", "test")
  commandLine(npm, "run", "build")

  dependsOn("npmInstall")
  dependsOn("generateProto")
}

tasks.check {
  dependsOn("npmCheck")
}

