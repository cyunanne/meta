package netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Client Connected : " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("channelRead~~~");

        String[] messages = ((String)msg).split(" ");
        String port = ctx.channel().remoteAddress().toString().split(":")[1];

        System.out.println("Client" + port + " : " + (String)msg);
        ctx.writeAndFlush((String)msg);
//        ctx.writeAndFlush("ready");
//        switch(messages[0]) {
//            case "put" -> {
//                FileReceiver fr = new FileReceiver(8889);
//                ctx.writeAndFlush((String)msg);
//                fr.run();
//            }
////            case "get" -> new FileReceiver(8889).run();
//        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        System.out.println("channelReadComplete");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }
}