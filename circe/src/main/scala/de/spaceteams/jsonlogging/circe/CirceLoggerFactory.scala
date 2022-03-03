package de.spaceteams.jsonlogging.circe

import de.spaceteams.jsonlogging.JsonLoggerFactory
import org.slf4j.spi.MDCAdapter

class CirceLoggerFactory(private val mdcAdapter: MDCAdapter)
    extends JsonLoggerFactory[CirceLogger] {
  override protected def mkRootLogger(name: String): CirceLogger =
    new CirceLogger(name = name, parent = rootLogger, mdcAdapter = mdcAdapter)
}

object CirceLoggerFactory {
  def apply(mdcAdapter: MDCAdapter) = new CirceLoggerFactory(mdcAdapter)
}
