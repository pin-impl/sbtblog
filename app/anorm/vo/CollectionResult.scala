package anorm.vo

case class CollectionResult[+T](
                               data: List[T],
                               next: Long
                               )