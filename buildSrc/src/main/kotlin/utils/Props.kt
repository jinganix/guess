package utils

import org.gradle.api.Project
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

object Props {
  private var initialized = false

  lateinit var group: String
  lateinit var version: String
  var verifyJavaDocs: Boolean = true
  var verifyCoverage: Boolean = true
  var jacocoMinCoverage: Double = 1.0

  fun initialize(project: Project) {
    if (initialized) {
      return
    }
    this::class.memberProperties.forEach {
      val key = it.name
      if (project.hasProperty(key)) {
        if (it is KMutableProperty<*>) {
          val value = project.property(key) as String
          when {
            (it.returnType.classifier == Int::class) -> {
              it.setter.call(this, value.toInt())
            }

            (it.returnType.classifier == Long::class) -> {
              it.setter.call(this, value.toLong())
            }

            (it.returnType.classifier == Float::class) -> {
              it.setter.call(this, value.toFloat())
            }

            (it.returnType.classifier == Double::class) -> {
              it.setter.call(this, value.toDouble())
            }

            (it.returnType.classifier == Boolean::class) -> {
              it.setter.call(this, value.toBoolean())
            }

            else -> {
              it.setter.call(this, value)
            }
          }
        }
      }
    }
    initialized = true
  }
}
