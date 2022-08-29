package de.spaceteams.jsonlogging

import de.spaceteams.jsonlogging.JsonLogger.logging_context
import org.slf4j.Marker
import org.slf4j.event.Level
import org.slf4j.helpers.LegacyAbstractLogger
import org.slf4j.helpers.MessageFormatter
import org.slf4j.spi.MDCAdapter

import java.io.PrintStream
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Instant
import java.time.format.DateTimeFormatter
import scala.collection.mutable
import scala.jdk.javaapi.CollectionConverters
import org.slf4j.Logger

abstract class JsonLogger(
    name: String,
    parent: JsonLogger,
    mdcAdapter: MDCAdapter,
    out: PrintStream
) extends LegacyAbstractLogger {

  override def getName: String = name

  private[jsonlogging] val fullName: String = {
    if (parent == null || parent.name == Logger.ROOT_LOGGER_NAME) {
      name
    } else {
      (parent.fullName + s".${name}").stripPrefix(
        s"${Logger.ROOT_LOGGER_NAME}."
      )
    }
  }

  val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT

  override def getFullyQualifiedCallerName: String = null

  protected def serialize(messageContext: Map[String, String]): String

  private var children: List[JsonLogger] = Nil

  protected def mkChildInstance(childName: String): JsonLogger

  private var level: Option[Level] = None
  private var levelInt: Int = Level.INFO.toInt

  def getLevel: Option[Level] = level

  def setLevel(newLevel: Option[Level]): Unit =
    synchronized {
      if (level == newLevel) return

      if (newLevel.isEmpty && Option(parent).isEmpty)
        throw new IllegalArgumentException(
          "The log level of the root logger cannot be unset."
        )

      level = newLevel

      newLevel.orElse(parent.getLevel).foreach { effectiveLevel =>
        levelInt = effectiveLevel.toInt
      }

      children.foreach(child => child.handleParentLevelChange(newLevel))
    }

  private[jsonlogging] def createChild(childName: String): JsonLogger = {
    children.find(_.getName == childName).getOrElse {
      synchronized {
        val instance = mkChildInstance(childName)

        instance.levelInt = levelInt

        children = instance :: children
        instance
      }
    }
  }

  private[jsonlogging] def handleParentLevelChange(
      newLevel: Option[Level]
  ): Unit = synchronized {
    if (level.isEmpty) {
      newLevel.foreach(l => {
        levelInt = l.toInt
        children.foreach(c => c.handleParentLevelChange(newLevel))
      })
    }
  }

  override def isTraceEnabled: Boolean = this.levelInt <= Level.TRACE.toInt

  override def isDebugEnabled: Boolean = this.levelInt <= Level.DEBUG.toInt

  override def isInfoEnabled: Boolean = this.levelInt <= Level.INFO.toInt

  override def isWarnEnabled: Boolean = this.levelInt <= Level.WARN.toInt

  override def isErrorEnabled: Boolean = this.levelInt <= Level.ERROR.toInt

  override def handleNormalizedLoggingCall(
      level: Level,
      marker: Marker,
      msg: String,
      arguments: Array[AnyRef],
      throwable: Throwable
  ): Unit = {
    val map = logging_context.headOption
      .map(_ => mutable.Map.from(logging_context))
      .getOrElse(mutable.Map.empty)

    Option(mdcAdapter.getCopyOfContextMap)
      .filter(m => !m.isEmpty)
      .foreach(mdc => map.addAll(CollectionConverters.asScala(mdc)))

    map.put("timestamp", dateTimeFormat.format(Instant.now()))
    map.put("level", level.toString)

    val message = MessageFormatter.basicArrayFormat(msg, arguments)
    map.put("message", message)

    map.put("logger", fullName)
    Option(throwable).foreach(t => {
      val sw = new StringWriter
      t.printStackTrace(new PrintWriter(sw))
      map.put("exception", sw.toString)
    })

    val outputString = serialize(map.toMap)

    out.println(outputString)
  }
}

object JsonLogger {

  val logging_context: Map[String, String] = sys.env.filter { case (k, _) =>
    k.toUpperCase.startsWith("LOGGING_CONTEXT_")
  }

}
