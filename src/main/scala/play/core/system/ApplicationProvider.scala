package play.core

import java.io._
import java.net._

import play.api._
import play.api.mvc._

/**
 * provides source code to be displayed on error pages
 */
trait SourceMapper {

  def sourceOf(className: String): Option[File]

  def sourceFor(e: Throwable): Option[(File, Int)] = {
    e.getStackTrace.find(element => sourceOf(element.getClassName).isDefined).map { interestingStackTrace =>
      sourceOf(interestingStackTrace.getClassName).get -> interestingStackTrace.getLineNumber
    }.map {
      case (source, line) => {
        play.templates.MaybeGeneratedSource.unapply(source).map { generatedSource =>
          generatedSource.source.get -> generatedSource.mapLine(line)
        }.getOrElse(source -> line)
      }
    }
  }

}

/**
 * generic layout for initialized Applications
 */
trait ApplicationProvider {
  def path: File
  def get: Either[Throwable, Application]
  def handleWebCommand(requestHeader: play.api.mvc.RequestHeader): Option[Result] = None
}

/**
 * creates and initializes an Application
 * @param applicationPath location of an Application
 */
class StaticApplication(applicationPath: File) extends ApplicationProvider {

  val application = new Application(applicationPath, this.getClass.getClassLoader, None, Mode.Prod)

  Play.start(application)

  def get = Right(application)
  def path = applicationPath
}

/**
 * wraps and starts a fake application (used in tests)
 * @param application fake Application
 */
class TestApplication(application: Application) extends ApplicationProvider {

  Play.start(application)

  def get = Right(application)
  def path = application.path
}

/**
 * represents an application that can be reloaded in Dev Mode
 */
class ReloadableApplication(sbtLink: String) extends ApplicationProvider {

  lazy val path = new java.io.File("sbtLink.projectPath")

  println(play.utils.Colors.magenta("--- (Running the application from SBT, auto-reloading is enabled) ---"))
  println()

  var lastState: Either[Throwable, Application] = Left(PlayException("Not initialized", "?"))

  def get = {

    synchronized {

      // Let's load the application on another thread
      // since we are still on the Netty IO thread.
      //
      // Because we are on DEV mode here, it doesn't really matter
      // but it's more coherent with the way it works in PROD mode.


        val reloaded = Right(None)
       
        reloaded.right.flatMap { maybeClassLoader =>

          val maybeApplication: Option[Either[Throwable, Application]] = maybeClassLoader.map { classloader =>
            try {

              if (lastState.isRight) {
                println()
                println(play.utils.Colors.magenta("--- (RELOAD) ---"))
                println()
              }

              val newApplication = new Application(path, classloader, Some(new SourceMapper {
                def sourceOf(className: String) = None
              }), Mode.Dev)

              Play.start(newApplication)

              Right(newApplication)
            } catch {
              case e: PlayException => {
                lastState = Left(e)
                lastState
              }
              case e: Throwable => {
                lastState = Left(UnexpectedException(unexpected = Some(e)))
                lastState
              }
            }
          }

          maybeApplication.flatMap(_.right.toOption).foreach { app =>
            lastState = Right(app)
          }

          maybeApplication.getOrElse(lastState)
        }

      

    }
  }

  override def handleWebCommand(request: play.api.mvc.RequestHeader): Option[Result] = {

    import play.api.mvc.Results._

    val applyEvolutions = """/@evolutions/apply/([a-zA-Z0-9_]+)""".r
    val resolveEvolutions = """/@evolutions/resolve/([a-zA-Z0-9_]+)/([0-9]+)""".r

    val documentation = """/@documentation""".r
    val book = """/@documentation/Book""".r
    val apiDoc = """/@documentation/api/(.*)""".r
    val wikiResource = """/@documentation/resources/(.*)""".r
    val wikiPage = """/@documentation/([^/]*)""".r

    val documentationHome = Option(System.getProperty("play.home")).map(ph => new java.io.File(ph + "/../documentation"))

    request.path match {

      case applyEvolutions(db) => {

        import play.api.db._
        import play.api.db.evolutions._

        Some {
          OfflineEvolutions.applyScript(path, Play.current.classloader, db)
          Redirect(request.queryString.get("redirect").filterNot(_.isEmpty).map(_(0)).getOrElse("/"))
        }
      }

      case resolveEvolutions(db, rev) => {

        import play.api.db._
        import play.api.db.evolutions._

        Some {
          OfflineEvolutions.resolve(path, Play.current.classloader, db, rev.toInt)
          Redirect(request.queryString.get("redirect").filterNot(_.isEmpty).map(_(0)).getOrElse("/"))
        }
      }

      case documentation() => {

        Some {
          Redirect("/@documentation/Home")
        }

      }

      case book() => {


        Some {
          documentationHome.flatMap { home =>
            Option(new java.io.File(home, "manual/book/Book")).filter(_.exists)
          }.map { book =>
            val pages = "Path(book).slurpString".split('\n').toSeq.map(_.trim)
            Ok(views.html.play20.book(pages))
          }.getOrElse(NotFound("Resource not found [Book]"))
        }

      }

      case apiDoc(page) => {

        Some {
          documentationHome.flatMap { home =>
            Option(new java.io.File(home, "api/" + page)).filter(f => f.exists && f.isFile)
          }.map { file =>
            Ok.sendFile(file, inline = true)
          }.getOrElse {
            Ok.sendFile(new java.io.File(""), inline = true)           
          }
        }

      }

      case wikiResource(path) => {

        Some {
          documentationHome.flatMap { home =>
            Option(new java.io.File(home, path)).filter(_.exists)
          }.map { file =>
            Ok.sendFile(file, inline = true)
          }.getOrElse(Ok.sendFile(new java.io.File(""), inline = true))
        }

      }

      case wikiPage(page) => {


        Some {

          val pageWithSidebar = documentationHome

          pageWithSidebar.map {x =>

              val linkRender: (String => (String, String)) = _ match {
                case link if link.contains("|") => {
                  val parts = link.split('|')
                  (parts.tail.head, parts.head)
                }
                case image if image.endsWith(".png") => {
                  val link = image match {
                    case full if full.startsWith("http://") => full
                    case absolute if absolute.startsWith("/") => "resources/manual" + absolute
                    case relative => "resources/" + ""
                  }
                  (link, """<img src="""" + link + """"/>""")
                }
                case link => {
                  (link, link)
                }
              }

              NotFound(views.html.play20.manual(page, None, None))
          }.getOrElse {
            NotFound(views.html.play20.manual(page, None, None))
          }

        }

      }

      case _ => None

    }
  }
}

