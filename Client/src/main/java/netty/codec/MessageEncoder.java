package netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.charset.Charset;
import java.util.List;

public class MessageEncoder extends MessageToMessageEncoder<String> {

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, List<Object> list) throws Exception {

        byte[] msgBytes = msg.getBytes(Charset.defaultCharset());
        short len = (short) msgBytes.length;

        ByteBuf data = Unpooled.buffer();
        data.writeByte((byte) 'M');
        data.writeShort(len);
        data.writeBytes(msgBytes);
        list.add(data);
    }
}
