package de.spaceteams.jsonlogging

trait ConfigurationService {
  def configure(factory: JsonLoggerFactory[_ <: JsonLogger]): Unit
}
