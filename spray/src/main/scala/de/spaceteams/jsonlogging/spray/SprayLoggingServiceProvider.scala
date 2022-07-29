package de.spaceteams.jsonlogging.spray

import de.spaceteams.jsonlogging.JsonLogger
import de.spaceteams.jsonlogging.JsonLoggerFactory
import de.spaceteams.jsonlogging.JsonLoggerServiceProvider

class SprayLoggingServiceProvider extends JsonLoggerServiceProvider {
  override val getLoggerFactory: JsonLoggerFactory[_ <: JsonLogger] =
    SprayLoggerFactory(
      getMDCAdapter
    )
}
