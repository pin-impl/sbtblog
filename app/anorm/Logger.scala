package anorm

import org.slf4j.spi.{ LocationAwareLogger => Slf4jLocationAwareLogger }
import org.slf4j.{Logger => Slf4jLogger, LoggerFactory}


trait Logging {
  protected lazy val log = Logger(this.getClass)
}

object Logger {

  def apply(clazz: Class[_]): Logger = {
    require(clazz != null, "clazz must not be null!")
    logger(LoggerFactory getLogger clazz)
  }

  def apply(name: String): Logger = {
    require(name != null, "loggerName must not be null!")
    logger(LoggerFactory getLogger name)
  }

  private def logger(slf4jLogger: Slf4jLogger): Logger = slf4jLogger match {
    case locationAwareLogger: Slf4jLocationAwareLogger =>
      new DefaultLocationAwareLogger(locationAwareLogger)
    case _ =>
      new DefaultLogger(slf4jLogger)
  }
}

trait Logger {

  lazy val name = slf4jLogger.getName

  protected val slf4jLogger: Slf4jLogger

  def error(msg: => String) {
    if (slf4jLogger.isErrorEnabled) slf4jLogger.error(msg)
  }

  def error(t: Throwable, msg: => String) {
    if (slf4jLogger.isErrorEnabled) slf4jLogger.error(msg, t)
  }

  def warn(msg: => String): Unit = {
    if (slf4jLogger.isWarnEnabled) slf4jLogger.warn(msg)
  }

  def warn(t: Throwable, msg: => String): Unit = {
    if (slf4jLogger.isWarnEnabled) slf4jLogger.warn(msg, t)
  }

  def info(msg: => String) {
    if (slf4jLogger.isInfoEnabled) slf4jLogger.info(msg)
  }

  def info(t: Throwable, msg: => String) {
    if (slf4jLogger.isInfoEnabled) slf4jLogger.info(msg, t)
  }

  def debug(msg: => String) {
    if (slf4jLogger.isDebugEnabled) slf4jLogger.debug(msg)
  }

  def debug(t: Throwable, msg: => String) {
    if (slf4jLogger.isDebugEnabled) slf4jLogger.debug(msg, t)
  }

  def trace(msg: => String) {
    if (slf4jLogger.isTraceEnabled) slf4jLogger.trace(msg)
  }

  def trace(t: Throwable, msg: => String) {
    if (slf4jLogger.isTraceEnabled) slf4jLogger.trace(msg, t)
  }
}

private final class DefaultLogger(override protected val slf4jLogger: Slf4jLogger) extends Logger

trait LocationAwareLogger extends Logger {

  import Slf4jLocationAwareLogger.{ERROR_INT, WARN_INT, INFO_INT, DEBUG_INT, TRACE_INT}

  override protected val slf4jLogger: Slf4jLocationAwareLogger

  protected val wrapperClassName: String

  override def error(msg: => String) {
    if (slf4jLogger.isErrorEnabled) log(ERROR_INT, msg)
  }

  override def error(t: Throwable, msg: => String) {
    if (slf4jLogger.isErrorEnabled) log(ERROR_INT, msg, t)
  }

  override def warn(msg: => String) {
    if (slf4jLogger.isWarnEnabled) log(WARN_INT, msg)
  }

  override def warn(t: Throwable, msg: => String) {
    if (slf4jLogger.isWarnEnabled) log(WARN_INT, msg, t)
  }

  override def info(msg: => String) {
    if (slf4jLogger.isInfoEnabled) log(INFO_INT, msg)
  }

  override def info(t: Throwable, msg: => String) {
    if (slf4jLogger.isInfoEnabled) log(INFO_INT, msg, t)
  }

  override def debug(msg: => String) {
    if (slf4jLogger.isDebugEnabled) log(DEBUG_INT, msg)
  }

  override def debug(t: Throwable, msg: => String) {
    if (slf4jLogger.isDebugEnabled) log(DEBUG_INT, msg, t)
  }

  override def trace(msg: => String) {
    if (slf4jLogger.isTraceEnabled) log(TRACE_INT, msg)
  }

  override def trace(t: Throwable, msg: => String) {
    if (slf4jLogger.isTraceEnabled) log(TRACE_INT, msg, t)
  }

  private final def log(level: Int, msg: String, throwable: Throwable = null) {
    slf4jLogger.log(null, wrapperClassName, level, msg, null, throwable)
  }
}

private object DefaultLocationAwareLogger {
  private val WrapperClassName = classOf[DefaultLocationAwareLogger].getName
}

private final class DefaultLocationAwareLogger(override protected val slf4jLogger: Slf4jLocationAwareLogger)
  extends LocationAwareLogger {
  override protected val wrapperClassName = DefaultLocationAwareLogger.WrapperClassName
}