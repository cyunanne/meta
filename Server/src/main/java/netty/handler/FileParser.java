package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.common.FileSpec;
import netty.common.Message;

public class FileParser extends ChannelInboundHandlerAdapter {

    private boolean flag = true;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ctx.fireChannelRead(msg);
//        if(flag) {
//            Message message = new Message(((ByteBuf) msg).readByte());
//            message.setLength(((ByteBuf) msg).readUnsignedShort());
//            flag =true;
//        } else {
//            ctx.fireChannelRead(msg);
//        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Message Channel Closed.");
    }

}