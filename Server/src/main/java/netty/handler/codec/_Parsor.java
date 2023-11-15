package netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import netty._test.Header;

import java.util.List;

public class _Parsor extends ReplayingDecoder<ByteBuf> {

    int size = 0;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {

        if(byteBuf.readableBytes() < 3) {
            return;
        }

//        byteBuf.markReaderIndex();
        Header header = new Header(byteBuf.readRetainedSlice(Header.HEADER_SIZE));
        int ri = byteBuf.readerIndex();


//        byte type = byteBuf.readByte();
//        int len = byteBuf.readUnsignedShort();
        int len = header.getLength();

        if(len != Short.MAX_VALUE) {
            System.out.println("??????????????????");
        }

        if(byteBuf.readableBytes() < len) {
//            byteBuf.resetReaderIndex();
            return;
        }

//        if(header.getType() == Header.TYPE_DATA)
          size += len;
//        System.out.println(size);
        if(size > 257_000_000)
            System.out.println(size);

        byte[] buf = new byte[len];
        byteBuf.readBytes(buf);

        list.add(buf);
//
        // 핸들러
//        switch (type) {
//            case 'M': list.add(new String(buf)); break; // msg(String)
//            case 'F': /*list.add(buf);*/ break; // file(byte[])
//        }
    }
}
