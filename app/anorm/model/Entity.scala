package anorm.model

import java.sql.{Connection, SQLException}

import anorm._
import scala.reflect.macros.whitebox
import anorm.AnormExtension._
import org.joda.time.DateTime

object Entity extends Logging {
  /*
   * 保存Entity。返回保存后的实体ID。Conn由参数提供。
   */
  def doSave(db: String, table: String, values: (String, Any)*)
            (implicit conn: Connection): Option[Long] = {
    log.info("Saving " + table)
    val i = values.map(x => s"`${x._1}`").mkString(",")
    val u = values.map(x => "{" + x._1 + "}").mkString(",")
    val dbPrefix = DBUtil.getDBPrefix(db)
    val q = s"insert into $dbPrefix`" + table + "`(" + i + ") values(" + u + ")"
    SQL(q).on(toAnormParam(values): _*).executeInsert()
  }


  /*
   * 更新Entity。返回更新的条数。
   */
  def doUpdate(db: String, table: String, id: Long, values: (String, Any)*)
              (implicit conn: Connection): Int = {
    if (id <= 0 || values.isEmpty) 0
    else {
      log.info("Updating " + table)
      val u = values.map(x => s"`${x._1}`" + "={" + x._1 + "}").mkString(",")
      val dbPrefix = DBUtil.getDBPrefix(db)
      val q = s"update $dbPrefix`" + table + "` set " + u + " where id={id}"
      SQL(q).on(toAnormParam(values): _*).on("id" -> id).executeUpdate()
    }
  }


  def getParser[A]()(implicit ma: Manifest[A]): RowParser[A] = {
    // Parser定义在Entity的Companion类
    val className = ma.runtimeClass.getName
    try {
      val companionClass = Class.forName(className + "$")
      val moduleField = companionClass.getField("MODULE$")
      val companionObject = moduleField.get(null)
      companionClass.getDeclaredMethod("parser").invoke(companionObject).asInstanceOf[RowParser[A]]
    } catch {
      case ex: Exception => throw new IllegalStateException("Cannot find RowParser for " + className)
    }
  }

  def toAnormParam[A](values: Seq[(String, Any)]): Seq[NamedParameter] =
    values.map{
      case (k, d: DateTime) => NamedParameter(k, d)
      case (k, d: Option[Any]) =>
        if (d.isDefined && d.get.isInstanceOf[DateTime])
          NamedParameter(k, d.get.asInstanceOf[DateTime])
        else if (d.isDefined && d.get.isInstanceOf[Enumeration#Value])
          NamedParameter(k, d.get.asInstanceOf[Enumeration#Value])
        else
          NamedParameter(k, anorm.Object(d.getOrElse(null)))
      case (k, e: Enumeration#Value) => NamedParameter(k, e)
      case (k, d: Seq[_]) => NamedParameter(k, d.mkString(","))
      case (k, v) => NamedParameter(k, anorm.Object(v))
    }

  def getTable(cls: Class[_]): String = {
    val anno = cls.getAnnotation(classOf[WithTable])
    if (anno != null) anno.value()
    else cls.getSimpleName
  }

  def getDb(cls: Class[_]): String = {
    cls.getAnnotation(classOf[WithDB]).value()
  }

  /**
    * 使用宏生成Entity的Anorm Parser。
    */
  def parser[A]: RowParser[A] = macro EntityMacro.generateParserImpl[A]

}


trait Entity[A] {
  self: A =>

  val id: Long

  /*
   * 保存或者更新Entity。返回Entity的ID，或者None。
   */
  def saveOrUpdate(): Long = {
    val (values, table, db) = getParams
    DB.withConnection(db) { implicit conn =>
      if (id > 0) {
        if (Entity.doUpdate(db, table, this.id, values: _*) > 0) id
        else throw new SQLException(s"Failed to update ${table}.")
      } else {
        Entity.doSave(db, table, values: _*).getOrElse(
          throw new SQLException(s"Failed to save ${table}."))
      }
    }
  }

  /*
   * 保存或者更新Entity。返回Entity的ID，或者None。
   */
  def saveOrUpdateInConn()(implicit conn: Connection): Long = {
    val (values, table, db) = getParams
    if (id > 0) {
      if (Entity.doUpdate(db, table, this.id, values: _*) > 0) id
      else throw new SQLException(s"Failed to update ${table}.")
    } else {
      Entity.doSave(db, table, values: _*).getOrElse(
        throw new SQLException(s"Failed to save ${table}."))
    }
  }


  private def getParams: (Array[(String, AnyRef)], String, String) = {
    val fields = self.getClass.getDeclaredFields
    val values = fields.filter(f => !f.getName.equalsIgnoreCase("id")).map {
      f =>
        f.setAccessible(true)
        (f.getName, f.get(self))
    }

    (values, Entity.getTable(self.getClass), Entity.getDb(self.getClass))
  }

}

object EntityMacro {

  def generateParserImpl[A: c.WeakTypeTag](c: whitebox.Context): c.Expr[RowParser[A]] = {
    import c.universe._

    // 获取构造方法的参数列表
    val companionSymbol = weakTypeOf[A].typeSymbol.companion
    val params = weakTypeOf[A].decls.collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor ⇒ m
    }.get.paramLists.head

    def getExtractValue(tree: Tree): String = tree match {
      case q"""$v = ${Literal(Constant(table: String))}""" => table
    }

    // 获取Entity对应的表名（默认为类名，除非有WithTable注解）
    val table = weakTypeOf[A].typeSymbol.annotations.
      find(_.tree.tpe <:< c.weakTypeOf[WithTable]).headOption.
      flatMap(_.tree.children.tail.headOption).map(t => getExtractValue(t)).
      getOrElse(companionSymbol.name.toString)

    // 生成parser的body部分
    val parserBodies = params.map { p: Symbol =>
      val symbol = Literal(Constant(s"${table}.${p.name.toString}"))
      q"""anorm.SqlParser.get[${p.typeSignature}](${symbol})"""
    }.reduce { (a1, a2) => q"${a1} ~ ${a2}"}

    // 生成parser的case部分
    val parserNames = params.zipWithIndex.map { case (x, idx) => TermName(s"_t$idx") }

    // 生成parser的对象构造部分
    val parserMap = parserNames.tail.foldLeft[Tree](pq"${parserNames.head}") { (a1, a2) => pq"${a1} ~ ${a2}" }

    val expr = c.Expr[RowParser[A]](
      q"""{
         import anorm.AnormExtension._
         import anorm.SqlParser._
         import anorm.~
         ${parserBodies} map { case ${parserMap} => ${companionSymbol}(..${parserNames}) }
       }"""
    )
    //println(s"$expr")
    expr
  }

}