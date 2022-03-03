package de.spaceteams.jsonlogging.spray

import de.spaceteams.jsonlogging.JsonLogger
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import org.slf4j.helpers.NOPMDCAdapter
import spray.json._

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
class SprayLoggerTest extends AnyWordSpec with Matchers {

  "A SprayLogger" should {
    "format a log event as JSON" in {
      val factory = LoggerFactory.getILoggerFactory
      val parent = factory.getLogger("root")

      val stream = new ByteArrayOutputStream()
      val printer = new PrintStream(stream)
      val logger = new SprayLogger(
        "test",
        parent.asInstanceOf[JsonLogger],
        new NOPMDCAdapter(),
        out = printer
      )
      logger.setLevel(Some(Level.INFO))
      logger.info("foobar")
      val output = stream.toString(StandardCharsets.UTF_8).parseJson.asJsObject

      output.getFields("message").map {
        case JsString(value) => value
        case _               => fail()
      } must contain("foobar")
    }
  }
}
