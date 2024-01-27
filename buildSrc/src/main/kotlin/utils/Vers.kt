package utils

import org.gradle.api.Project
import java.util.*
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

object Vers {
  private var initialized = false

  lateinit var versionAssertj: String
  lateinit var versionAuthorizationServer: String
  lateinit var versionBcpkix: String
  lateinit var versionCaffeine: String
  lateinit var versionCommonsCodec: String
  lateinit var versionCommonsLang3: String
  lateinit var versionCoverallsGradlePlugin: String
  lateinit var versionDependencyManagementPlugin: String
  lateinit var versionFlyway: String
  lateinit var versionFreemarker: String
  lateinit var versionGradleVersionsPlugin: String
  lateinit var versionGuava: String
  lateinit var versionJackson: String
  lateinit var versionJacocoAgent: String
  lateinit var versionJakartaXml: String
  lateinit var versionJupiter: String
  lateinit var versionJwt: String
  lateinit var versionLombok: String
  lateinit var versionMapstruct: String
  lateinit var versionMockitoCore: String
  lateinit var versionMockitoInline: String
  lateinit var versionModelMapper: String
  lateinit var versionMysqlConnector: String
  lateinit var versionNetty: String
  lateinit var versionPeashooter: String
  lateinit var versionProtobuf: String
  lateinit var versionProtobufGradlePlugin: String
  lateinit var versionProtobufJava: String
  lateinit var versionSpotlessPluginGradle: String
  lateinit var versionSpringBootGradlePlugin: String
  lateinit var versionTestContainers: String
  lateinit var versionWebpb: String

  fun initialize(project: Project, override: Properties = Properties()) {
    if (initialized) {
      return
    }
    this::class.memberProperties.forEach {
      if (it !is KMutableProperty<*>) {
        return
      }
      val key = it.name
      if (override.containsKey(key)) {
        it.setter.call(this, override.getProperty(key))
      } else if (project.hasProperty(key)) {
        it.setter.call(this, project.property(key))
      }
    }
    initialized = true
  }
}
