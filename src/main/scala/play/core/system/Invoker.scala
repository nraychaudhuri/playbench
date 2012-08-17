package play.core


import com.typesafe.config._

import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.iteratee._
import play.api.http.HeaderNames._

import play.utils._

/**
 * holds Play's internal invokers
 */
class Invoker(applicationProvider: Option[ApplicationProvider] = None) {



  def stop(): Unit = {}
}

/**
 * provides Play's internal actor system and the corresponding actor instances
 */
object Invoker {

  /**
   * provides an extractor for body parser
   */
  //case class GetBodyParser(request: RequestHeader, bodyParser: BodyParser[_])

  /**
   * provides actor helper
   */
  //case class HandleAction[A](request: Request[A], response: Response, action: Action[A], app: Application)

  private var invokerOption: Option[Invoker] = None

  private def invoker: Invoker = invokerOption.getOrElse {
    val default = new Invoker()
    invokerOption = Some(default)
    Logger.info("Invoker was created outside of Invoker#init - this potentially could lead to initialization problems in production mode")
    default
  }



  /**
   * contructor used by Server
   */
  def apply(applicationProvider: ApplicationProvider): Invoker = new Invoker(Some(applicationProvider))

  /**
   * saves invoker instance in global scope
   */
  def init(invoker: Invoker): Unit = {
    
  }

  /**
   * removes invoker instance from global scope
   */
  def uninit(): Unit = {
    invokerOption = None
  }


}

object Agent {

 

}

