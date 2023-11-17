package netty.handler;

import io.netty.channel.*;

public class MessageHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        System.out.println((String) msg);
    }

}
