package services.blog

import anorm._
import anorm.model.BlogVO
import javax.inject.Singleton

@Singleton
class BlogService {

  def listBlogs:List[BlogVO] = {
    val parser: RowParser[BlogVO]
    SQL(
      """
        | select * from blog
      """.stripMargin).as()

  }
}
