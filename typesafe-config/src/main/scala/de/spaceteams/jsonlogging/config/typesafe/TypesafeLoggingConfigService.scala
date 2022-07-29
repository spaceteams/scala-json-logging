package de.spaceteams.jsonlogging.config.typesafe

import com.typesafe.config.ConfigFactory
import de.spaceteams.jsonlogging.ConfigurationService
import de.spaceteams.jsonlogging.JsonLogger
import de.spaceteams.jsonlogging.JsonLoggerFactory
import org.slf4j.event.Level

class TypesafeLoggingConfigService extends ConfigurationService {
  override def configure(factory: JsonLoggerFactory[_ <: JsonLogger]): Unit = {
    val config = ConfigFactory.load()
    val loggingConfig = config.getConfig("logging").getConfig("levels")
    loggingConfig.entrySet().iterator().forEachRemaining { entry =>
      val key = entry.getKey.stripPrefix("\"").stripSuffix("\"")
      val logger = factory.getLogger(key)
      val level = Level.valueOf(loggingConfig.getString(entry.getKey))
      logger.asInstanceOf[JsonLogger].setLevel(Some(level))
    }
  }
}
