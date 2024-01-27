import utils.Vers.versionCommonsLang3
import utils.Vers.versionJupiter

plugins {
  `maven-publish`
  application
  id("java.common")
  id("org.springframework.boot")
  java
  signing
}

dependencies {
  implementation("org.apache.commons:commons-lang3:${versionCommonsLang3}")
  testImplementation("org.junit.jupiter:junit-jupiter-api:${versionJupiter}")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${versionJupiter}")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
