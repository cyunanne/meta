package netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Client Connected : " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("channelRead");

        String message = msg instanceof byte[] ? new String((byte[])msg) : (String)msg;
        String port = ctx.channel().remoteAddress().toString().split(":")[1];
        if ("quit".equals(message)) {
            ctx.close();
            return;
        }
        System.out.println("Client" + port + " : " + message);

        switch(message) {
            case "put" -> {
                FileReceiver fr = new FileReceiver(8889);
                ctx.writeAndFlush("ready");
                fr.run();
            }
//            case "get" -> new FileReceiver(8889).run();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
//        System.out.println("channelReadComplete");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }
}