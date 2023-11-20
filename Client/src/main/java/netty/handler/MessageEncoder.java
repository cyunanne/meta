package netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import netty.common.Message;

import java.util.List;

/**
 * Message to ByteBuf
 */
public class MessageEncoder extends MessageToMessageEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message msg, List<Object> list) throws Exception {
        list.add(msg.getByteBuf());
    }
}
