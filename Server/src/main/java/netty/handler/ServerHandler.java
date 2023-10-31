package netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.test.Header;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Client Connected : " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("channelRead~~~");

        if(msg instanceof String) {
            String port = ctx.channel().remoteAddress().toString().split(":")[1];
            System.out.println("Client" + port + " : " + (String)msg);
            ctx.writeAndFlush((String)msg);
        } else {
            ByteArrayInputStream bis = new ByteArrayInputStream((byte[]) msg);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Header header = (Header) ois.readObject();
        }

//        String[] messages = ((String)msg).split(" ");
//        ctx.writeAndFlush("ready");
//        switch(messages[0]) {
//            case "put" -> {z
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