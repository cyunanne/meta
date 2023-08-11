package netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client Connected : " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        String message = ((ByteBuf) msg).toString(Charset.defaultCharset());
        String port = ctx.channel().remoteAddress().toString().split(":")[1];

        if ("quit".equals(message)) {
            ctx.close();
            return;
        }

        System.out.println("Client" + port + " : " + message);

        // send answer
        String tmp = "Okay! You " + port;
        ByteBuf buf = Unpooled.buffer().writeBytes(tmp.getBytes());
        ctx.writeAndFlush(buf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }

}