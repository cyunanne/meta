package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;

public class TransferDataBuilder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {

        // 파일 데이터
        if(msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) msg;
            Header header = new Header(Header.TYPE_DATA, Header.CMD_GET, false, buf.readableBytes());
            TransferData td = new TransferData(header, buf);
            ctx.writeAndFlush(td);

        // 메타 데이터
        } else if(msg instanceof FileSpec) {
            ByteBuf buf = ((FileSpec) msg).toByteBuf();
            Header header = new Header(Header.TYPE_META, Header.CMD_GET, true, buf.readableBytes());
            TransferData td = new TransferData(header, buf);
            ctx.writeAndFlush(td);

        // 그 외
        } else {
            ctx.writeAndFlush(msg);
        }

    }

}