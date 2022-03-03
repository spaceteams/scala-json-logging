package de.spaceteams.jsonlogging.spray

import de.spaceteams.jsonlogging.JsonLoggerFactory
import org.slf4j.spi.MDCAdapter

class SprayLoggerFactory(private val mdcAdapter: MDCAdapter)
    extends JsonLoggerFactory[SprayLogger] {
  override protected def mkRootLogger(name: String): SprayLogger =
    new SprayLogger(name = name, parent = rootLogger, mdcAdapter = mdcAdapter)
}

object SprayLoggerFactory {
  def apply(mdcAdapter: MDCAdapter) = new SprayLoggerFactory(mdcAdapter)
}
