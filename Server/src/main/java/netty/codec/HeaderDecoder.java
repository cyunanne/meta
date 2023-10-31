package netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import netty.test.Header;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

public class HeaderDecoder extends MessageToMessageDecoder<ByteBuf> {

    private static final int HEADER_SIZE = 58;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes() < HEADER_SIZE) {
            return;
        }

        byteBuf.markReaderIndex();

        byte hData[] = new byte[HEADER_SIZE];
        byteBuf.readBytes(hData);

        ByteArrayInputStream bis = new ByteArrayInputStream(hData);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Header header = (Header) ois.readObject();

        int bodylen = header.getSize();

        //	Body read
        if(byteBuf.readableBytes() < bodylen) {
            byteBuf.resetReaderIndex();
            return;
        }

        //	Body Parse
        byte bData[] = new byte[bodylen];
        byteBuf.readBytes(bData);

        switch (header.getType()) {
            case 'M':
                list.add(new String(bData));
                break;
            case 'F':
                list.add(bData);
                break;
        }
    }
}
