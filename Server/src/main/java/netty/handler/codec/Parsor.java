package netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import netty._test.Header;
import netty._test.TransferData;

import java.util.List;

public class Parsor extends ReplayingDecoder<ByteBuf> {

    int size = 0;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        // 헤더가 모두 들어올 때 까지 대기
        if(in.readableBytes() < Header.HEADER_SIZE) {
            return;
        }

        ByteBuf buf = in.readBytes(Header.HEADER_SIZE);
        Header header = new Header(buf);

        // 데이터가 모두 들어올 때 까지 대기
        int len = header.getLength();
        if(in.readableBytes() < len) {
            return;
        }

        byte[] data = new byte[len];
        in.readBytes(data);
        out.add(data);

        size += len;
        if(size > 257_000_000)
            System.out.println(size);
    }
}
