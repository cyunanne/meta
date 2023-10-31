package netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import netty.test.Header;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

public class HeaderParsor extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes() < Header.HEADER_SIZE) {
            return;
        }

        byteBuf.markReaderIndex();

        // header read
        byte hData[] = new byte[Header.HEADER_SIZE];
        byteBuf.readBytes(hData);

        // header parse
        ByteArrayInputStream bis = new ByteArrayInputStream(hData);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Header header = (Header) ois.readObject();

        //	Body read
        int bodylen = header.getSize();
        if(byteBuf.readableBytes() < bodylen) {
            byteBuf.resetReaderIndex();
            return;
        }

        //	Body Parse
        byte bData[] = new byte[bodylen];
        byteBuf.readBytes(bData);

        // 다음 핸들러로 패스
        switch (header.getType()) {
            case 'M':
                list.add(new String(bData)); // msg(String)
                break;
            case 'F':
                list.add(bData); // file(byte[])
                break;
        }
    }
}
