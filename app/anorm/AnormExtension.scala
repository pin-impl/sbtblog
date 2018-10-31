package anorm

import scala.reflect.runtime.universe._
import java.lang.reflect.InvocationTargetException
import java.math.BigInteger

import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

object AnormExtension {
  val dateFormatGeneration: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmssSS")

  private lazy val rm = scala.reflect.runtime.currentMirror

  implicit val enumToStatement = new ToStatement[Enumeration#Value] {
    def set(s: java.sql.PreparedStatement, index: Int, value: Enumeration#Value) {
      s.setInt(index, value.id)
    }
  }

  implicit def rowToEnumValue[E <: Enumeration: TypeTag]: Column[E#Value] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, _, _) = meta
    value match {
      case int: Int => {
        try {
          val methodSym = typeTag[E].tpe.member(TermName("getOrDefault")).asMethod
          val im = rm.reflect(rm.reflectModule(typeOf[E].termSymbol.asModule).instance)
          Right(im.reflectMethod(methodSym)(int).asInstanceOf[E#Value])
        } catch {
          case e: InvocationTargetException if e.getCause.isInstanceOf[NoSuchElementException] =>
            Left(SqlMappingError(s"Can't convert $int to ${typeTag[E].tpe}, because it isn't a valid value: $qualified"))

        }
      }
      case _ => Left(TypeDoesNotMatch(s"Can't convert [$value:${value.asInstanceOf[AnyRef].getClass}] to ${typeTag[E].tpe}: $qualified"))
    }
  }

  implicit def rowToDateTime: Column[DateTime] = Column.nonNull {
    (value, meta) =>
      val MetaDataItem(qualified, nullable, clazz) = meta
      value match {
        case ts: java.sql.Timestamp => Right(new DateTime(ts.getTime))
        case d: java.sql.Date => Right(new DateTime(d.getTime))
        case str: java.lang.String => Right(dateFormatGeneration.parseDateTime(str))
        case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass))
      }
  }

  implicit val dateTimeToStatement = new ToStatement[DateTime] {
    def set(s: java.sql.PreparedStatement, index: Int, value: DateTime) {
      s.setTimestamp(index, new java.sql.Timestamp(value.withMillisOfSecond(0).getMillis))
    }
  }

  implicit val optionDateTimeToStatement = new ToStatement[Option[DateTime]] {
    def set(s: java.sql.PreparedStatement, index: Int, value: Option[DateTime]) {
      s.setTimestamp(index, value.map(t => new java.sql.Timestamp(t.withMillisOfSecond(0).getMillis)).getOrElse(null))
    }
  }

  implicit def rowToByteArray: Column[Array[Byte]] = Column.nonNull {
    (value, meta) =>
      val MetaDataItem(qualified, nullable, clazz) = meta
      value match {
        case data: Array[Byte] => Right(data)
        case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass + " to Byte Array for column " + qualified))
      }
  }

  implicit def rowToLong: Column[Long] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case int: Int => Right(int: Long)
      case long: Long => Right(long)
      case bi: BigInteger => Right(bi.longValue())
      case bi: BigDecimal => Right(bi.longValue())
      case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass + " to Long for column " + qualified))
    }
  }


  def bytes(columnName: String): RowParser[Array[Byte]] =
    SqlParser.get[Array[Byte]](columnName)(implicitly[Column[Array[Byte]]])
}
