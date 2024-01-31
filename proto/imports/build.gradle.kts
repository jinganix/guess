import utils.Vers.versionWebpb

plugins {
  java
}

repositories {
  mavenCentral()
}

dependencies {
  compileOnly("io.github.jinganix.webpb:webpb-proto:${versionWebpb}")
}
