package de.spaceteams.jsonlogging.circe

import de.spaceteams.jsonlogging.JsonLogger
import de.spaceteams.jsonlogging.JsonLoggerFactory
import de.spaceteams.jsonlogging.JsonLoggerServiceProvider

class CirceLoggingServiceProvider extends JsonLoggerServiceProvider {
  override val getLoggerFactory: JsonLoggerFactory[_ <: JsonLogger] =
    CirceLoggerFactory(
      getMDCAdapter
    )
}
