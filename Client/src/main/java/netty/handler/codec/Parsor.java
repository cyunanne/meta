package netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class Parsor extends ReplayingDecoder<ByteBuf> {

//    int size = 0;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {

        if(byteBuf.readableBytes() < 3) {
            return;
        }

//        byteBuf.markReaderIndex();

        byte type = byteBuf.readByte();
        int len = byteBuf.readUnsignedShort();

        if(byteBuf.readableBytes() < len) {
//            byteBuf.resetReaderIndex();
            return;
        }

//        size += len;
//        System.out.println(size);
        byte[] buf = new byte[len];
        byteBuf.readBytes(buf);
//
        // 핸들러
        switch (type) {
            case 'M': list.add(new String(buf)); break; // msg(String)
            case 'F': list.add(buf); break; // file(byte[])
            default: System.out.println("메시지 파싱 에러"); break;
        }
    }
}
