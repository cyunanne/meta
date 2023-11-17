package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.MessageTransfer;
import netty.common.FileSpec;
import netty.common.Message;

public class MessageParser extends ChannelInboundHandlerAdapter {

    private MessageTransfer messageTransfer;

    public MessageParser(MessageTransfer messageTransfer) {
        this.messageTransfer = messageTransfer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Message Channel Connected.");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        Message message = new Message(buf.readByte());
        message.setLength(buf.readUnsignedShort());
        message.setData(buf.readBytes(message.getLength()));

        String str = buf.readBytes(message.getLength()).toString(io.netty.util.CharsetUtil.UTF_8);
        System.out.println("Server: " + str);

        messageTransfer.send(message);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Message Channel Closed.");
    }

}