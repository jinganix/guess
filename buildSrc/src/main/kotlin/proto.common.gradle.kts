import utils.Props
import utils.Vers
import utils.Vers.versionWebpb
import utils.hierarchicalGroup

plugins {
  id("conventions.common")
  java
}

Props.initialize(project)
Vers.initialize(project)

group = hierarchicalGroup()

repositories {
  mavenLocal()
  mavenCentral()
}

dependencies {
  compileOnly("io.github.jinganix.webpb:webpb-proto:${versionWebpb}")
}
