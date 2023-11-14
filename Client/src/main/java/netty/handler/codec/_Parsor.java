package netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class _Parsor extends ReplayingDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {

        // Header
        if(byteBuf.readableBytes() < 3) {
            return;
        }

        byte type = byteBuf.readByte();
        int len = byteBuf.readUnsignedShort();


        // Data
        if(byteBuf.readableBytes() < len) {
            return;
        }

        byte[] buf = new byte[len];
        byteBuf.readBytes(buf);

        // 읽은 데이터(헤더) 버퍼에서 삭제
//        byteBuf.discardReadBytes();

        // Pass to Handler
        switch (type) {
            case 'M': list.add(byteBuf.toString(Charset.defaultCharset())); break; // msg(String)
            case 'F': list.add(buf); break; // file(byte[])
//            case 'F': list.add(byteBuf); break; // file(ByteBuf)
            default: System.out.println("메시지 파싱 에러"); break;
        }
    }
}
