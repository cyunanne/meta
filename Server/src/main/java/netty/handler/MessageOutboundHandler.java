package netty.handler;

import io.netty.channel.*;
import netty.common.Message;

public class MessageOutboundHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {

        // 문자열 전송 > StringEncoder 전달
        if(msg instanceof String) {
            ctx.writeAndFlush(msg);

            // Message 타입 전송 > casting to ByteBuf > send to server directly
        } else if(msg instanceof Message) {
            Message message = (Message) msg;
            ctx.write(message.getByteBuf());
            ctx.writeAndFlush(message.getData());
        }
    }

}
