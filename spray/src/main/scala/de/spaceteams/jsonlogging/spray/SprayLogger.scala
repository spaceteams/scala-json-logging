package de.spaceteams.jsonlogging.spray

import de.spaceteams.jsonlogging.JsonLogger
import org.slf4j.spi.MDCAdapter
import spray.json._

import java.io.PrintStream

import DefaultJsonProtocol._

class SprayLogger(
    name: String,
    parent: JsonLogger,
    mdcAdapter: MDCAdapter,
    out: PrintStream = System.out
) extends JsonLogger(name, parent, mdcAdapter, out) {
  override protected def serialize(
      messageContext: Map[String, String]
  ): String = {
    messageContext.toJson.compactPrint
  }

  override protected def mkChildInstance(childName: String): JsonLogger =
    new SprayLogger(childName, this, mdcAdapter, out)
}
