package netty.handler.old.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import netty.common.Header;
import netty.common.TransferData;

import java.util.List;

public class Parsor extends ReplayingDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {

        // 헤더가 모두 들어올 때 까지 대기
        if(byteBuf.readableBytes() < Header.HEADER_SIZE) {
            return;
        }

        Header header = new Header(byteBuf.readSlice(Header.HEADER_SIZE));

        // 데이터가 모두 들어올 때 까지 대기
        int len = header.getLength();
        if(byteBuf.readableBytes() < len) {
            return;
        }

        ByteBuf data = byteBuf.readRetainedSlice(len);

        // Pass to Handler
        list.add(new TransferData(header, data));
    }
}
