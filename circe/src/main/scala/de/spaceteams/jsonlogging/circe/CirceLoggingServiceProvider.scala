package de.spaceteams.jsonlogging.circe

import de.spaceteams.jsonlogging.JsonLoggerServiceProvider
import org.slf4j.ILoggerFactory

class CirceLoggingServiceProvider extends JsonLoggerServiceProvider {
  override def getLoggerFactory: ILoggerFactory = CirceLoggerFactory(
    getMDCAdapter
  )
}
