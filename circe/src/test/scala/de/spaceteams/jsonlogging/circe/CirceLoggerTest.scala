package de.spaceteams.jsonlogging.circe

import de.spaceteams.jsonlogging.JsonLogger
import io.circe.parser
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import org.slf4j.helpers.NOPMDCAdapter

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
class CirceLoggerTest extends AnyWordSpec with Matchers {

  "A CirceLogger" should {
    "format a log event as JSON" in {
      val factory = LoggerFactory.getILoggerFactory
      val parent = factory.getLogger("root")

      val stream = new ByteArrayOutputStream()
      val printer = new PrintStream(stream)
      val logger = new CirceLogger(
        "test",
        parent.asInstanceOf[JsonLogger],
        new NOPMDCAdapter(),
        out = printer
      )
      logger.setLevel(Some(Level.INFO))
      logger.info("foobar")
      val output =
        parser.parse(stream.toString(StandardCharsets.UTF_8)).getOrElse(???)

      output.asObject.flatMap(o =>
        o.toMap.get("message").flatMap(_.asString)
      ) must contain("foobar")
    }
  }
}
