package de.spaceteams.jsonlogging

import org.slf4j.ILoggerFactory
import org.slf4j.Logger
import org.slf4j.event.Level

import java.util.concurrent.ConcurrentHashMap
import scala.annotation.tailrec

abstract class JsonLoggerFactory[T <: JsonLogger] extends ILoggerFactory {

  private val loggers = new ConcurrentHashMap[String, Logger]()

  protected def mkRootLogger(name: String): T

  protected val rootLogger: T = {
    val l = mkRootLogger(JsonLoggerFactory.rootLoggerName)
    l.setLevel(Some(Level.INFO))
    l
  }

  @tailrec
  private def buildChildLogger(
      prefix: List[String],
      names: List[String],
      parent: JsonLogger
  ): JsonLogger = {
    def createChild(name: List[String]): JsonLogger = {
      val c = parent.createChild(name.reverse.mkString("."))
      loggers.put(c.getName, c)
      c
    }
    names match {
      case Nil         => parent
      case head :: Nil => createChild(head :: prefix)
      case head :: tail =>
        val p = head :: prefix
        buildChildLogger(p, tail, createChild(p))
    }
  }

  override def getLogger(name: String): Logger = {
    if (name == JsonLoggerFactory.rootLoggerName)
      return rootLogger

    Option(loggers.get(name)).getOrElse({
      val names = name.split('.').toList
      buildChildLogger(Nil, names, rootLogger)
    })
  }
}

object JsonLoggerFactory {
  val rootLoggerName = org.slf4j.Logger.ROOT_LOGGER_NAME
}
