package netty.handler.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import netty.common.Header;
import netty.common.TransferData;

import java.util.List;

public class Parser extends ReplayingDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) {

        // 헤더가 모두 들어올 때 까지 대기
        if(byteBuf.readableBytes() < Header.HEADER_SIZE) {
            return;
        }

        ByteBuf headerData = byteBuf.readSlice(Header.HEADER_SIZE);
        Header header = new Header(headerData.retain());

        // 데이터가 모두 들어올 때 까지 대기
        int len = header.getLength();
        if(byteBuf.readableBytes() < len) {
            return;
        }

        // Pass to Handler
        ByteBuf data = byteBuf.readSlice(len);
        list.add(new TransferData(header, data.retain()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ctx.channel().disconnect();
    }
}
