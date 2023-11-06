package netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class FileEncoder extends MessageToMessageEncoder<byte[]> {

    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] file, List<Object> list) throws Exception {
        ByteBuf data = Unpooled.buffer();
        data.writeByte((byte) 'F');
        data.writeShort(file.length);
        data.writeBytes(file);
        list.add(data);
    }
}
