package de.spaceteams.jsonlogging

import org.slf4j.ILoggerFactory
import org.slf4j.Logger
import org.slf4j.event.Level

import java.util.concurrent.ConcurrentHashMap

abstract class JsonLoggerFactory[T <: JsonLogger] extends ILoggerFactory {

  private val loggers = new ConcurrentHashMap[String, JsonLogger]()

  protected def mkRootLogger(name: String): T

  protected val rootLogger: T = {
    val l = mkRootLogger(JsonLoggerFactory.rootLoggerName)
    l.setLevel(Some(Level.INFO))
    l
  }

  private def buildChildLogger(
      names: List[String],
      parent: JsonLogger
  ): JsonLogger = {
    names.foldLeft(parent)((p, name) => {
      val child = p.createChild(name)
      child
    })
  }

  override def getLogger(name: String): Logger = {
    if (name == JsonLoggerFactory.rootLoggerName)
      return rootLogger

    loggers.computeIfAbsent(
      name,
      (_: String) => {
        val names = name.split('.').toList
        buildChildLogger(names, rootLogger)
      }
    )
  }
}

object JsonLoggerFactory {
  val rootLoggerName = org.slf4j.Logger.ROOT_LOGGER_NAME
}
