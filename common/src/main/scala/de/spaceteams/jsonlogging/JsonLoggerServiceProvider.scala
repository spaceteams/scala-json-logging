package de.spaceteams.jsonlogging

import org.slf4j.IMarkerFactory
import org.slf4j.helpers.BasicMDCAdapter
import org.slf4j.helpers.BasicMarkerFactory
import org.slf4j.spi.MDCAdapter
import org.slf4j.spi.SLF4JServiceProvider

import java.util.ServiceLoader

abstract class JsonLoggerServiceProvider extends SLF4JServiceProvider {

  private val REQUIRED_VERSION = "2.0.99"

  private val markerFactory = new BasicMarkerFactory()
  private val mdcAdapter = new BasicMDCAdapter()

  override val getLoggerFactory: JsonLoggerFactory[_ <: JsonLogger]

  override val getMarkerFactory: IMarkerFactory = markerFactory

  override val getMDCAdapter: MDCAdapter = mdcAdapter

  override def getRequestedApiVersion: String = REQUIRED_VERSION

  override def initialize(): Unit = {
    val loader = ServiceLoader.load(classOf[ConfigurationService])
    loader.forEach(consumer => consumer.configure(getLoggerFactory))
  }
}
