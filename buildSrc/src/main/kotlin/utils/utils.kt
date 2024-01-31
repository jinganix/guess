package utils

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.DocsType
import org.gradle.api.attributes.Usage
import org.gradle.kotlin.dsl.named

fun Project.hierarchicalGroup(): String {
  var suffix = ""
  var proj = project.parent
  while (rootProject != proj && proj != null) {
    suffix = "." + proj.name + suffix
    proj = proj.parent!!
  }
  return project.group.toString() + suffix
}

fun Project.createConfiguration(
  name: String,
  docsType: String,
  configuration: Action<Configuration>
): Configuration {
  val conf = configurations.create(name) {
    isVisible = false
    attributes {
      attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
      attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
      attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named(docsType))
    }
  }
  configuration.execute(conf)
  return conf
}
