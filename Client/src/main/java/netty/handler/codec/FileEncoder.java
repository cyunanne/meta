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
//public class FileEncoder extends MessageToMessageEncoder<ByteBuf> {

    /*@Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf data, List<Object> list) throws Exception {
        ByteBuf header = Unpooled.buffer();
        header.writeByte((byte) 'F');
        header.writeShort(data.readableBytes());
        list.add(header);
        list.add(data);
    }*/

    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] data, List<Object> list) throws Exception {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte((byte) 'F');
        buf.writeShort(data.length);
        buf.writeBytes(data);
        list.add(buf);
    }
}
