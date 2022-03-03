package de.spaceteams.jsonlogging.spray

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.slf4j.LoggerFactory

class SprayLoggingServiceProviderTest extends AnyWordSpec with Matchers {

  "A SprayLoggingServiceProviderTest" should {
    "provide a logger" in {
      val factory = LoggerFactory.getILoggerFactory
      val logger = factory.getLogger("simple-logger")
      logger.getName must be("simple-logger")
      logger mustBe a[SprayLogger]
    }
  }
}
