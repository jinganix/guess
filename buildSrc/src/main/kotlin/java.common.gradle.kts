import utils.Vers
import utils.Vers.versionAssertj
import utils.Vers.versionJakartaXml
import utils.Vers.versionLombok
import utils.Vers.versionMockitoCore
import utils.Vers.versionMockitoInline
import utils.createConfiguration

plugins {
  id("com.diffplug.spotless")
  id("conventions.common")
  jacoco
  java
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
  annotationProcessor("org.projectlombok:lombok:${versionLombok}")
  compileOnly("jakarta.xml.bind:jakarta.xml.bind-api:${versionJakartaXml}")
  compileOnly("org.projectlombok:lombok:${versionLombok}")
  testAnnotationProcessor("org.projectlombok:lombok:${versionLombok}")
  testCompileOnly("org.projectlombok:lombok:${versionLombok}")
  testImplementation("org.assertj:assertj-core:${versionAssertj}")
  testImplementation("org.mockito:mockito-core:${versionMockitoCore}")
  testImplementation("org.mockito:mockito-inline:${versionMockitoInline}")
}

tasks.test {
  useJUnitPlatform()
}

spotless {
  java {
    targetExclude("build/**/*")
    googleJavaFormat()
  }
}

tasks.check {
  dependsOn(tasks.spotlessApply)
}

jacoco {
  toolVersion = Vers.versionJacocoAgent
}

tasks.jacocoTestReport {
  enabled = false
}

createConfiguration("outgoingClassDirs", "classDirs") {
  extendsFrom(configurations.implementation.get())
  isCanBeResolved = false
  isCanBeConsumed = true
  sourceSets.main.get().output.forEach {
    outgoing.artifact(it)
  }
}

createConfiguration("outgoingSourceDirs", "sourceDirs") {
  extendsFrom(configurations.implementation.get())
  isCanBeResolved = false
  isCanBeConsumed = true
  sourceSets.main.get().java.srcDirs.forEach {
    outgoing.artifact(it)
  }
}

createConfiguration("outgoingCoverageData", "coverageData") {
  extendsFrom(configurations.implementation.get())
  isCanBeResolved = false
  isCanBeConsumed = true
  outgoing.artifact(tasks.test.map {
    it.extensions.getByType<JacocoTaskExtension>().destinationFile!!
  })
}
