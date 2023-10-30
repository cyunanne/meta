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

        System.out.println("Server : " + (String)msg);

        if(messages[0].equals("put")) {
            FileSender sender = new FileSender("localhost", 8889, messages[1]);
            sender.run();

            if(sender.getChannel().isWritable()) {
                System.out.println("파일전송완료 메시지를 보내고싶은 시점");
            }
//            ctx.writeAndFlush("fin");
//        } else if(messages[0].equals("fin")) {
//            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }
}