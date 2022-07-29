package de.spaceteams.jsonlogging.config.typesafe

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.slf4j.LoggerFactory

class TypesafeLoggingConfigServiceTest extends AnyWordSpec with Matchers {

  "A TypesafeLoggingConfigService" should {
    "configure logging" in {
      val logger = LoggerFactory.getLogger("de.spaceteams.logging")
      logger.isDebugEnabled shouldBe true

      LoggerFactory.getLogger("de.spaceteams").isTraceEnabled shouldBe true
      LoggerFactory.getLogger("de.spaceteams.foo").isTraceEnabled shouldBe true

      LoggerFactory.getLogger("foo").isDebugEnabled shouldBe false
    }
  }
}
