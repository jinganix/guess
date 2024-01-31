plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "guess"
include(":frontend:weapp")
include(":proto:imports")
include(":proto:internal")
include(":proto:service")
include(":service:guess")
