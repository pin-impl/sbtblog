package services.blog

import anorm._
import anorm.model.BlogVO
import javax.inject.Singleton

@Singleton
class BlogService {

  def listBlogs:List[BlogVO] = {
    SQL(
      """
        | select * from blog
      """.stripMargin).as()

  }
}
