package anorm


import java.sql.{PreparedStatement, Timestamp}

import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

object AnormExtension {
  val dateFormatGeneration: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss SS")

  implicit def rowToDateTime: Column[DateTime] = Column.nonNull { (value, meta) =>

    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case ts: java.sql.Timestamp => Right(new DateTime(ts.getTime))
      case d: java.sql.Date => Right(new DateTime(d.getTime))
      case str: java.lang.String => Right(dateFormatGeneration.parseDateTime(str))
      case _ => Left(TypeDoesNotMatch(s"Cannot convert $value : ${value.asInstanceOf[AnyRef].getClass}"))
    }
  }

  implicit val dateTimeToStatement: ToStatement[DateTime] = (s: PreparedStatement, index: Int, v: DateTime) => {
    v.getMillis
    s.setTimestamp(index, new Timestamp(v.withMillisOfSecond(0).getMillis))
  }
}
