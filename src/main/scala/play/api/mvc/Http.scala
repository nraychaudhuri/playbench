package play.mvc {

  abstract class Call {
    val rand = new java.util.Random
    def url(): String 
    def method(): String
    def unique():play.api.mvc.Call = {
        var url = this.url()
        if(url.indexOf('?') == -1) {
            url = url + "?" + rand.nextLong();
        } else {
            url = url + "&" + rand.nextLong();
        }
        return new play.api.mvc.Call(method(), url);
    }

  }
}
package play.api.mvc {

  import play.api._
  import play.api.libs.iteratee._
  import play.api.libs.Crypto

  import scala.annotation._

  /**
   * The HTTP request header. Note that it doesn’t contain the request body yet.
   */
  @implicitNotFound("Cannot find any HTTP Request Header here")
  trait RequestHeader {

    /**
     * The complete request URI, containing both path and query string.
     */
    def uri: String

    /**
     * The URI path.
     */
    def path: String

    /**
     * The HTTP method.
     */
    def method: String

    /**
     * The parsed query string.
     */
    def queryString: Map[String, Seq[String]]

    /**
     * Helper method to access a queryString parameter.
     */
    def getQueryString(key: String): Option[String] = queryString.get(key).flatMap(_.headOption)

    /**
     * The HTTP headers.
     */
    def headers: Headers

    /**
     * The client IP address.
     */
    def remoteAddress: String

    /**
     * The HTTP host (domain, optionally port)
     */
    lazy val host: String = headers.get(play.api.http.HeaderNames.HOST).getOrElse("")

    /**
     * The HTTP domain
     */
    lazy val domain: String = host.split(':').head

    /**
     * The Request Langs, extracted from the Accept-Language header.
     */
    lazy val acceptLanguages: Seq[play.api.i18n.Lang] = {
      try {
        headers.get(play.api.http.HeaderNames.ACCEPT_LANGUAGE).map { acceptLanguage =>
          acceptLanguage.split("\\s*,\\s*").map(l => play.api.i18n.Lang(l.split(";").head)).toSeq
        }.getOrElse(Nil)
      } catch {
        case e: Throwable => e.printStackTrace(); Nil
      }
    }

    /**
     * @return The media types set in the request Accept header, not sorted in any particular order.
     */
    lazy val accept: Seq[String] = {
      for {
        acceptHeader <- headers.get(play.api.http.HeaderNames.ACCEPT).toSeq
        value <- acceptHeader.split(",")
        contentType <- value.split(";").headOption
      } yield contentType
    }

    /**
     * Check if this request accepts a given media type.
     * @returns true if `mediaType` matches the Accept header, otherwise false
     */
    def accepts(mediaType: String): Boolean = {
      accept.contains(mediaType) || accept.contains("*/*") || accept.contains(mediaType.takeWhile(_ != '/') + "/*")
    }

    /**
     * The HTTP cookies.
     */
    lazy val cookies: Cookies = Cookies(headers.get(play.api.http.HeaderNames.COOKIE))

    /**
     * Parses the `Session` cookie and returns the `Session` data.
     */
    lazy val session: Session = Session.decodeFromCookie(cookies.get(Session.COOKIE_NAME))

    /**
     * Parses the `Flash` cookie and returns the `Flash` data.
     */
    lazy val flash: Flash = Flash.decodeFromCookie(cookies.get(Flash.COOKIE_NAME))

    /**
     * Returns the raw query string.
     */
    lazy val rawQueryString: String = uri.split('?').drop(1).mkString("?")

    /**
     * Returns the value of the Content-Type header (without the ;charset= part if exists)
     */
    lazy val contentType: Option[String] = headers.get(play.api.http.HeaderNames.CONTENT_TYPE).flatMap(_.split(';').headOption).map(_.toLowerCase)

    /**
     * Returns the charset of the request for text-based body
     */
    lazy val charset: Option[String] = headers.get(play.api.http.HeaderNames.CONTENT_TYPE).flatMap(_.split(';').tail.headOption).map(_.toLowerCase.trim).filter(_.startsWith("charset=")).flatMap(_.split('=').tail.headOption)

    override def toString = {
      method + " " + uri
    }

  }

  /**
   * The complete HTTP request.
   *
   * @tparam A the body content type.
   */
  @implicitNotFound("Cannot find any HTTP Request here")
  trait Request[+A] extends RequestHeader {
    self =>

    /**
     * The body content.
     */
    def body: A

    /**
     * Transform the request body.
     */
    def map[B](f: A => B): Request[B] = new Request[B] {
      def uri = self.uri
      def path = self.path
      def method = self.method
      def queryString = self.queryString
      def headers = self.headers
      def remoteAddress = self.remoteAddress
      lazy val body = f(self.body)
    }

  }


  object Request {

    def apply[A](rh:RequestHeader,a:A) = new Request[A] {
           def uri = rh.uri
           def path = rh.path
           def method = rh.method
           def queryString = rh.queryString
           def headers = rh.headers
           lazy val remoteAddress = rh.remoteAddress
           def username = None
           val body = a
    }
  }

  /**
   * Wrap an existing request. Useful to extend a request.
   */
  class WrappedRequest[A](request: Request[A]) extends Request[A] {
    def body = request.body
    def headers = request.headers
    def queryString = request.queryString
    def path = request.path
    def uri = request.uri
    def method = request.method
    def remoteAddress = request.remoteAddress
  }

  /**
   * The HTTP response.
   */
  @implicitNotFound("Cannot find any HTTP Response here")
  trait Response {

    /**
     * Handles a result.
     *
     * Depending on the result type, it will be sent synchronously or asynchronously.
     */
    def handle(result: Result): Unit

  }

  /**
   * Defines a `Call`, which describes an HTTP request and can be used to create links or fill redirect data.
   *
   * These values are usually generated by the reverse router.
   *
   * @param method the request HTTP method
   * @param url the request URL
   */
  case class Call(method: String, url: String) extends play.mvc.Call {

    /**
     * Transform this call to an absolute URL.
     */
    def absoluteURL(secure: Boolean = false)(implicit request: RequestHeader) = {
      "http" + (if (secure) "s" else "") + "://" + request.host + this.url
    }

    /**
     * Transform this call to an WebSocket URL.
     */
    def webSocketURL(secure: Boolean = false)(implicit request: RequestHeader) = {
      "ws" + (if (secure) "s" else "") + "://" + request.host + this.url
    }

    override def toString = url

  }

  /**
   * The HTTP headers set.
   */
  trait Headers {

    /**
     * Optionally returns the first header value associated with a key.
     */
    def get(key: String): Option[String] = getAll(key).headOption

    /**
     * Retrieves the first header value which is associated with the given key.
     */
    def apply(key: String): String = get(key).getOrElse(scala.sys.error("Header doesn't exist"))

    /**
     * Retrieve all header values associated with the given key.
     */
    def getAll(key: String): Seq[String]

    /**
     * Retrieve all header keys
     */
    def keys: Set[String]

    /**
     * Transform the Headers to a Map
     */
    def toMap: Map[String, Seq[String]] = keys.map { headerKey =>
      (headerKey, getAll(headerKey))
    }.toMap

    /**
     * Transform the Headers to a Map by ignoring multiple values.
     */
    def toSimpleMap: Map[String, String] = keys.map { headerKey =>
      (headerKey, apply(headerKey))
    }.toMap

  }

  /**
   * Trait that should be extended by the Cookie helpers.
   */
  trait CookieBaker[T <: AnyRef] {

    /**
     * The cookie name.
     */
    val COOKIE_NAME: String

    /**
     * Default cookie, returned in case of error or if missing in the HTTP headers.
     */
    val emptyCookie: T

    /**
     * `true` if the Cookie is signed. Defaults to false.
     */
    val isSigned: Boolean = false

    /**
     * `true` if the Cookie should have the httpOnly flag, disabling access from Javascript. Defaults to true.
     */
    val httpOnly = true

    /**
     * The cookie expiration date in seconds, `-1` for a transient cookie
     */
    val maxAge = -1

    /**
     * `true` if the Cookie should have the secure flag, restricting usage to https. Defaults to false.
     */
    val secure = false

    /**
     * Encodes the data as a `String`.
     */
    def encode(data: Map[String, String]): String = {
      val encoded = java.net.URLEncoder.encode(data.filterNot(_._1.contains(":")).map(d => d._1 + ":" + d._2).mkString("\u0000"), "UTF-8")
      if (isSigned)
        Crypto.sign(encoded) + "-" + encoded
      else
        encoded
    }

    /**
     * Decodes from an encoded `String`.
     */
    def decode(data: String): Map[String, String] = {

      def urldecode(data: String) = java.net.URLDecoder.decode(data, "UTF-8").split("\u0000").map(_.split(":")).map(p => p(0) -> p.drop(1).mkString(":")).toMap

      try {
        if (isSigned) {
          val splitted = data.split("-")
          val message = splitted.tail.mkString("-")
          if (splitted(0) == Crypto.sign(message))
            urldecode(message)
          else
            Map.empty[String, String]
        } else urldecode(data)
      } catch {
        // fail gracefully is the session cookie is corrupted
        case _: Throwable => Map.empty[String, String]
      }
    }

    /**
     * Encodes the data as a `Cookie`.
     */
    def encodeAsCookie(data: T): Cookie = {
      val cookie = encode(serialize(data))
      Cookie(COOKIE_NAME, cookie, maxAge, "/", None, secure, httpOnly)
    }

    /**
     * Decodes the data from a `Cookie`.
     */
    def decodeFromCookie(cookie: Option[Cookie]): T = {
      cookie.filter(_.name == COOKIE_NAME).map(c => deserialize(decode(c.value))).getOrElse(emptyCookie)
    }

    /**
     * Builds the cookie object from the given data map.
     *
     * @param data the data map to build the cookie object
     * @return a new cookie object
     */
    protected def deserialize(data: Map[String, String]): T

    /**
     * Converts the given cookie object into a data map.
     *
     * @param cookie the cookie object to serialize into a map
     * @return a new `Map` storing the key-value pairs for the given cookie
     */
    protected def serialize(cookie: T): Map[String, String]

  }

  /**
   * HTTP Session.
   *
   * Session data are encoded into an HTTP cookie, and can only contain simple `String` values.
   */
  case class Session(data: Map[String, String] = Map.empty[String, String]) {

    /**
     * Optionally returns the session value associated with a key.
     */
    def get(key: String) = data.get(key)

    /**
     * Returns `true` if this session is empty.
     */
    def isEmpty: Boolean = data.isEmpty

    /**
     * Adds a value to the session, and returns a new session.
     *
     * For example:
     * {{{
     * session + ("username" -> "bob")
     * }}}
     *
     * @param kv the key-value pair to add
     * @return the modified session
     */
    def +(kv: (String, String)) = copy(data + kv)

    /**
     * Removes any value from the session.
     *
     * For example:
     * {{{
     * session - "username"
     * }}}
     *
     * @param key the key to remove
     * @return the modified session
     */
    def -(key: String) = copy(data - key)

    /**
     * Retrieves the session value which is associated with the given key.
     */
    def apply(key: String) = data(key)

  }

  /**
   * Helper utilities to manage the Session cookie.
   */
  object Session extends CookieBaker[Session] {
    val COOKIE_NAME = Play.maybeApplication.flatMap(_.configuration.getString("session.cookieName")).getOrElse("PLAY_SESSION")
    val emptyCookie = new Session
    override val isSigned = true
    override val secure = Play.maybeApplication.flatMap(_.configuration.getBoolean("session.secure")).getOrElse(false)
    override val maxAge = Play.maybeApplication.flatMap(_.configuration.getInt("session.maxAge")).getOrElse(-1)
    override val httpOnly = Play.maybeApplication.flatMap(_.configuration.getBoolean("session.httpOnly")).getOrElse(true)

    def deserialize(data: Map[String, String]) = new Session(data)

    def serialize(session: Session) = session.data
  }

  /**
   * HTTP Flash scope.
   *
   * Flash data are encoded into an HTTP cookie, and can only contain simple `String` values.
   */
  case class Flash(data: Map[String, String] = Map.empty[String, String]) {

    /**
     * Optionally returns the flash value associated with a key.
     */
    def get(key: String) = data.get(key)

    /**
     * Returns `true` if this flash scope is empty.
     */
    def isEmpty: Boolean = data.isEmpty

    /**
     * Adds a value to the flash scope, and returns a new flash scope.
     *
     * For example:
     * {{{
     * flash + ("success" -> "Done!")
     * }}}
     *
     * @param kv the key-value pair to add
     * @return the modified flash scope
     */
    def +(kv: (String, String)) = copy(data + kv)

    /**
     * Removes a value from the flash scope.
     *
     * For example:
     * {{{
     * flash - "success"
     * }}}
     *
     * @param key the key to remove
     * @return the modified flash scope
     */
    def -(key: String) = copy(data - key)

    /**
     * Retrieves the flash value that is associated with the given key.
     */
    def apply(key: String) = data(key)

  }

  /**
   * Helper utilities to manage the Flash cookie.
   */
  object Flash extends CookieBaker[Flash] {

    val COOKIE_NAME = Play.maybeApplication.flatMap(_.configuration.getString("flash.cookieName")).getOrElse("PLAY_FLASH")
    val emptyCookie = new Flash

    def deserialize(data: Map[String, String]) = new Flash(data)

    def serialize(flash: Flash) = flash.data

  }

  /**
   * An HTTP cookie.
   *
   * @param name the cookie name
   * @param value the cookie value
   * @param maxAge the cookie expiration date in seconds, `-1` for a transient cookie, or `0` for a cookie that expires now
   * @param path the cookie path, defaulting to the root path `/`
   * @param domain the cookie domain
   * @param secure whether this cookie is secured, sent only for HTTPS requests
   * @param httpOnly whether this cookie is HTTP only, i.e. not accessible from client-side JavaScipt code
   */
  case class Cookie(name: String, value: String, maxAge: Int = -1, path: String = "/", domain: Option[String] = None, secure: Boolean = false, httpOnly: Boolean = true)

  /**
   * The HTTP cookies set.
   */
  trait Cookies {

    /**
     * Optionally returns the cookie associated with a key.
     */
    def get(name: String): Option[Cookie]

    /**
     * Retrieves the cookie that is associated with the given key.
     */
    def apply(name: String): Cookie = get(name).getOrElse(scala.sys.error("Cookie doesn't exist"))

  }

  /**
   * Helper utilities to encode Cookies.
   */
  object Cookies {

    import scala.collection.JavaConverters._

    // We use netty here but just as an API to handle cookies encoding
    import org.jboss.netty.handler.codec.http.{ CookieEncoder, CookieDecoder, DefaultCookie }

    /**
     * Extract cookies from the Set-Cookie header.
     */
    def apply(header: Option[String]) = new Cookies {

      lazy val cookies: Map[String, Cookie] = header.map(Cookies.decode(_)).getOrElse(Seq.empty).groupBy(_.name).mapValues(_.head)

      def get(name: String) = cookies.get(name)
      override def toString = cookies.toString

    }

    /**
     * Encodes cookies as a proper HTTP header.
     *
     * @param cookies the Cookies to encode
     * @param discard discard these cookies as well
     * @return a valid Set-Cookie header value
     */
    def encode(cookies: Seq[Cookie], discard: Seq[String] = Nil): String = {
      val encoder = new CookieEncoder(true)
      cookies.foreach { c =>
        encoder.addCookie {
          val nc = new DefaultCookie(c.name, c.value)
          nc.setMaxAge(c.maxAge)
          nc.setPath(c.path)
          c.domain.map(nc.setDomain(_))
          nc.setSecure(c.secure)
          nc.setHttpOnly(c.httpOnly)
          nc
        }
      }
      discard.foreach { n =>
        encoder.addCookie {
          val nc = new DefaultCookie(n, "")
          nc.setMaxAge(0)
          nc
        }
      }
      encoder.encode()
    }

    /**
     * Decodes a Set-Cookie header value as a proper cookie set.
     *
     * @param cookieHeader the Set-Cookie header value
     * @return decoded cookies
     */
    def decode(cookieHeader: String): Seq[Cookie] = {
      new CookieDecoder().decode(cookieHeader).asScala.map { c =>
        Cookie(c.getName, c.getValue, c.getMaxAge, Option(c.getPath).getOrElse("/"), Option(c.getDomain), c.isSecure, c.isHttpOnly)
      }.toSeq
    }

    /**
     * Merges an existing Set-Cookie header with new cookie values
     *
     * @param cookieHeader the existing Set-Cookie header value
     * @param cookies the new cookies to encode
     * @param discard discard these cookies as well
     * @return a valid Set-Cookie header value
     */
    def merge(cookieHeader: String, cookies: Seq[Cookie], discard: Seq[String] = Nil): String = {
      encode(cookies ++ decode(cookieHeader), discard)
    }

  }

}