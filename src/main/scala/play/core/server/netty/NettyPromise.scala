package play.core.server.netty

import org.jboss.netty.channel.ChannelFuture
import play.api.libs.concurrent._
import scala.concurrent.{ ExecutionContext, CanAwait}
import java.util.concurrent.TimeUnit
import org.jboss.netty.channel.ChannelFutureListener

/**
 * provides a play.api.libs.concurrent.Promise implementation based on Netty's
 * ChannelFuture
 */
object NettyPromise {

  def apply(channelPromise: ChannelFuture):scala.concurrent.Future[Unit] = throw new RuntimeException
}
