package old;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Server Connected");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String message = "";
//        String message = msg instanceof byte[] ? new String((byte[])msg) : (String)msg;

        if(msg instanceof byte[])
            message = new String((byte[])msg);
        else if(msg instanceof String)
            message = (String)msg;
        else if(msg instanceof ByteBuf)
            message = ((ByteBuf) msg).toString(Charset.defaultCharset());
        else {
            ctx.fireChannelRead(msg);
            return;
        }

        System.out.println("Server : " + message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }
}