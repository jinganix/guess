import org.apache.tools.ant.taskdefs.condition.Os

plugins {
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

