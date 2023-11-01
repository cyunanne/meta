package netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class Parsor extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {

        if(byteBuf.readableBytes() < 3) {
            return;
        }

        // mark so we can reset here if the message isn't complete yet
        byteBuf.markReaderIndex();

        byte type = byteBuf.readByte();
        int len = byteBuf.readUnsignedShort();

        if(byteBuf.readableBytes() < len) {
            byteBuf.resetReaderIndex();
            return;
        }

        byte data[] = new byte[len];
        byteBuf.readBytes(data);

        // 핸들러로 패스
        switch (type) {
            case 'M': list.add(new String(data)); break; // msg(String)
            case 'F': list.add(data); break; // file(byte[])
        }
    }
}
