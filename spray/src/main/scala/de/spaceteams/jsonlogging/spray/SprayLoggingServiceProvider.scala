package de.spaceteams.jsonlogging.spray

import de.spaceteams.jsonlogging.JsonLoggerServiceProvider
import org.slf4j.ILoggerFactory

class SprayLoggingServiceProvider extends JsonLoggerServiceProvider {
  override def getLoggerFactory: ILoggerFactory = SprayLoggerFactory(
    getMDCAdapter
  )
}
