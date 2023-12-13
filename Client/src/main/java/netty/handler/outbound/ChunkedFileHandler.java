package netty.handler.outbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import netty.common.Builder;

public class ChunkedFileHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {

        if(msg instanceof ByteBuf) ctx.writeAndFlush(Builder.wrap((ByteBuf) msg));
        else                       ctx.writeAndFlush(msg);

    }

}