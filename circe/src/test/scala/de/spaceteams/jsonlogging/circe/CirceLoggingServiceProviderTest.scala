package de.spaceteams.jsonlogging.circe

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.slf4j.LoggerFactory

class CirceLoggingServiceProviderTest extends AnyWordSpec with Matchers {

  "A CirceLoggingServiceProviderTest" should {
    "provide a logger" in {
      val factory = LoggerFactory.getILoggerFactory
      val logger = factory.getLogger("simple-logger")
      logger.getName must be("simple-logger")
      logger mustBe a[CirceLogger]
    }
  }
}
