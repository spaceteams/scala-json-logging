package de.spaceteams.jsonlogging

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.slf4j.event.Level

class JsonLoggerTest extends AnyWordSpec with Matchers {

  "A JsonLogger" when {
    "being the root logger" should {
      "not allow unsetting the log level" in {
        val logger = new JsonLoggerFactory[JsonLogger]() {
          override protected def mkRootLogger(name: String): JsonLogger =
            new JsonLogger(name, null, null, null) {
              override protected def serialize(
                  messageContext: Map[String, String]
              ): String = ???

              override protected def mkChildInstance(
                  childName: String
              ): JsonLogger = ???
            }
        }.getLogger(JsonLoggerFactory.rootLoggerName)
        an[IllegalArgumentException] should be thrownBy logger
          .asInstanceOf[JsonLogger]
          .setLevel(None)
      }
    }
    "given a log level" should {
      def getLogger = new JsonLogger("logger", null, null, null) {
        override protected def serialize(
            messageContext: Map[String, String]
        ): String = ???

        override protected def mkChildInstance(childName: String): JsonLogger =
          ???
      }
      "handle ERROR" in {
        val logger = getLogger
        logger.setLevel(Some(Level.ERROR))

        logger.isErrorEnabled mustBe true
        logger.isWarnEnabled mustBe false
        logger.isInfoEnabled mustBe false
        logger.isDebugEnabled mustBe false
        logger.isTraceEnabled mustBe false
      }
      "handle WARN" in {
        val logger = getLogger
        logger.setLevel(Some(Level.WARN))

        logger.isErrorEnabled mustBe true
        logger.isWarnEnabled mustBe true
        logger.isInfoEnabled mustBe false
        logger.isDebugEnabled mustBe false
        logger.isTraceEnabled mustBe false
      }
      "handle INFO" in {
        val logger = getLogger
        logger.setLevel(Some(Level.INFO))

        logger.isErrorEnabled mustBe true
        logger.isWarnEnabled mustBe true
        logger.isInfoEnabled mustBe true
        logger.isDebugEnabled mustBe false
        logger.isTraceEnabled mustBe false
      }
      "handle DEBUG" in {
        val logger = getLogger
        logger.setLevel(Some(Level.DEBUG))

        logger.isErrorEnabled mustBe true
        logger.isWarnEnabled mustBe true
        logger.isInfoEnabled mustBe true
        logger.isDebugEnabled mustBe true
        logger.isTraceEnabled mustBe false
      }
      "handle TRACE" in {
        val logger = getLogger
        logger.setLevel(Some(Level.TRACE))

        logger.isErrorEnabled mustBe true
        logger.isWarnEnabled mustBe true
        logger.isInfoEnabled mustBe true
        logger.isDebugEnabled mustBe true
        logger.isTraceEnabled mustBe true
      }
    }

    "having children" should {

      def mkLogger(name: String, parent: JsonLogger): JsonLogger = {
        new JsonLogger(name, parent, null, null) {
          override protected def serialize(
              messageContext: Map[String, String]
          ): String = ???

          override protected def mkChildInstance(
              childName: String
          ): JsonLogger = mkLogger(childName, this)
        }
      }
      "default to parents level" in {
        val parent = mkLogger("de.spaceteams", null)

        parent.setLevel(Some(Level.TRACE))

        val child = parent.createChild("de.spaceteams.logging.child")
        child.isTraceEnabled mustBe true
      }

      "respect a child's level if set" in {
        val parent = mkLogger("de.spaceteams", null)

        parent.setLevel(Some(Level.WARN))

        val child = parent.createChild("de.spaceteams.logging.child")
        child.isDebugEnabled mustBe false
        child.setLevel(Some(Level.DEBUG))
        child.isDebugEnabled mustBe true

        parent.setLevel(Some(Level.INFO))
        parent.isInfoEnabled mustBe true
        parent.isDebugEnabled mustBe false

        child.isDebugEnabled mustBe true
      }

      "fall back to a parents log level if child is reset" in {
        val parent = mkLogger("de.spaceteams", null)

        parent.setLevel(Some(Level.WARN))

        val child = parent.createChild("de.spaceteams.logging.child")
        child.isInfoEnabled mustBe false
        child.setLevel(Some(Level.INFO))
        child.isInfoEnabled mustBe true

        child.setLevel(None)
        child.isInfoEnabled mustBe false
        child.isWarnEnabled mustBe true
      }
    }
  }
}
