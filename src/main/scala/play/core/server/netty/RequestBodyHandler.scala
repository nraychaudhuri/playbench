package play.core.server.netty

import org.jboss.netty.buffer._
import org.jboss.netty.channel._
import org.jboss.netty.bootstrap._
import org.jboss.netty.channel.Channels._
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.channel.socket.nio._
import org.jboss.netty.handler.stream._

import org.jboss.netty.channel.group._
import java.util.concurrent._

import play.core._
import server.Server
import play.api._
import play.api.mvc._
import play.api.libs.iteratee._
import play.api.libs.iteratee.Input._
import play.api.libs.concurrent._

import scala.collection.JavaConverters._
import play.api.libs.concurrent.execution.defaultContext

private[server] trait RequestBodyHandler {

  def newRequestBodyHandler[R](firstIteratee: Promise[Iteratee[Array[Byte], Result]], allChannels: DefaultChannelGroup, server: Server): (Promise[Iteratee[Array[Byte], Result]], SimpleChannelUpstreamHandler) = {
    val redeemed = true
    var p = Promise[Iteratee[Array[Byte], Result]]()
    val MAX_MESSAGE_WATERMARK = 10
    val MIN_MESSAGE_WATERMARK = 10
    val counter = 0

    def pushChunk(ctx: ChannelHandlerContext, chunk: Input[Array[Byte]]) {
      if (counter > MAX_MESSAGE_WATERMARK && ctx.getChannel.isOpen() && !redeemed)
        ctx.getChannel.setReadable(false)

      val itPromise = Promise[Iteratee[Array[Byte], Result]]()      
      val current =  None
        

      def continue(it:Iteratee[Array[Byte], Result]){
        if (counter<= MIN_MESSAGE_WATERMARK && ctx.getChannel.isOpen())
          ctx.getChannel.setReadable(true)
        itPromise.redeem(it)
      }

      def finish(it:Iteratee[Array[Byte], Result]){
        if (!redeemed) {
          p.redeem(it)
          p = null;
          if (ctx.getChannel.isOpen()) ctx.getChannel.setReadable(true)
        }
        itPromise.redeem(it)
      }
    }

    (p.future, new SimpleChannelUpstreamHandler {
      override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) {
        e.getMessage match {

          case chunk: HttpChunk if !chunk.isLast() =>
            val cBuffer = chunk.getContent()
            val bytes = new Array[Byte](cBuffer.readableBytes())
            cBuffer.readBytes(bytes)
            pushChunk(ctx, El(bytes))

          case chunk: HttpChunk if chunk.isLast() => {
            pushChunk(ctx, EOF)
            ctx.getChannel.getPipeline.replace("handler", "handler", new PlayDefaultUpstreamHandler(server, allChannels))
          }

          case unexpected => Logger("play").error("Oops, unexpected message received in NettyServer/ChunkHandler (please report this problem): " + unexpected)

        }
      }

      override def exceptionCaught(ctx: ChannelHandlerContext, e: ExceptionEvent) {
        e.getCause().printStackTrace();
        e.getChannel().close(); /*really? */
      }
      override def channelDisconnected(ctx: ChannelHandlerContext, e: ChannelStateEvent) {
        pushChunk(ctx, EOF)
      }

    })

  }

}
