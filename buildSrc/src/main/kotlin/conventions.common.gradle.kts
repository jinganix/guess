import utils.Props
import utils.Vers

plugins {
  id("conventions.versioning")
  idea
}

Props.initialize(project)
Vers.initialize(project)

repositories {
  mavenCentral()
}
