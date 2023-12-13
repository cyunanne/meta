package netty.handler.outbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import netty.common.Builder;
import netty.common.FileSpec;
import netty.common.Header;

public class DownloadReqHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {

        if( msg instanceof FileSpec ) {
            ctx.writeAndFlush(Builder.wrap((FileSpec) msg, Header.CMD_GET));
        }
    }

}