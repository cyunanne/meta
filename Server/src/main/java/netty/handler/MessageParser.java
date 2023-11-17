package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.Message;

import java.io.FileNotFoundException;

public class MessageParser extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Message Channel Connected.");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        Message message = new Message(buf.readByte());
        message.setLength(buf.readUnsignedShort());

        if(message.isSignal()) {
            ctx.fireChannelRead(msg);
        } else {
            FileSpec fs = new FileSpec(buf.readBytes(message.getLength()));
            ctx.fireChannelRead(fs);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Message Channel Closed.");
    }

}