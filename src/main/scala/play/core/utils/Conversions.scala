package play.utils

import java.util.concurrent.{ TimeUnit, Callable }

/**
 * provides conversion helpers
 */
object Conversions {

  def newMap[A, B](data: (A, B)*) = Map(data: _*)

  
}
