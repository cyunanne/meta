package netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class _FileEncoder extends MessageToMessageEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf data, List<Object> list) throws Exception {
//        System.out.println("FileEncoderByteBuf.encode()");
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte((byte) 'F');
        buf.writeShort(data.readableBytes());
        buf.writeBytes(data);
        list.add(buf);
    }
}
