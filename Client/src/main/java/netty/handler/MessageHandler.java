package netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.FileSender;
import netty.NettyClient;

public class MessageHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String[] messages = ((String)msg).split(" ");

        System.out.print("Server : " + (String)msg + "\n>>> ");

//        if(messages[0].equals("put")) {
//            new FileSender("localhost", 8889, messages[1]).run();
//            ctx.writeAndFlush("__fin__");
//
//        } else if(messages[0].equals("quit")) {
//            ctx.close();
//        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }
}