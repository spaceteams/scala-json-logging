package de.spaceteams.jsonlogging.circe

import de.spaceteams.jsonlogging.JsonLogger
import io.circe.Json
import org.slf4j.spi.MDCAdapter

import java.io.PrintStream

class CirceLogger(
    name: String,
    parent: JsonLogger,
    mdcAdapter: MDCAdapter,
    out: PrintStream = System.out
) extends JsonLogger(name, parent, mdcAdapter, out) {
  override protected def serialize(
      messageContext: Map[String, String]
  ): String = {
    val map = messageContext.map { case (k, v) => k -> Json.fromString(v) }
    Json.fromFields(map).deepDropNullValues.noSpaces
  }

  override protected def mkChildInstance(childName: String): JsonLogger =
    new CirceLogger(childName, this, mdcAdapter, out)

}
