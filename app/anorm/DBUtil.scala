package anorm

object DBUtil {

  def getDBName(db: String): String = {
    if (db.equals("blog")) "blog" else db
  }

  def getDBPrefix(db: String): String = {
    if(db.equals("blog")) s"$db." else ""
  }
}
