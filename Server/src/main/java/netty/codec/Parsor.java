package netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class Parsor extends ReplayingDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {

        if(byteBuf.readableBytes() < 3) {
            return;
        }

        byteBuf.markReaderIndex();

        byte type = byteBuf.readByte();
        int len = byteBuf.readShort();

        if(byteBuf.readableBytes() < len) {
            byteBuf.resetReaderIndex();
            return;
        }

        byte data[] = new byte[len];
        byteBuf.readBytes(data);

        // 핸들러
        switch (type) {
            case 'M': list.add(new String(data)); break; // msg(String)
            case 'F': list.add(data); break; // file(byte[])
        }
    }
}
