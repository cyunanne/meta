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

        // 메타 데이터
        if(msg instanceof FileSpec) {
            FileSpec fs = (FileSpec) msg;
            ByteBuf buf = fs.toByteBuf();
            Header header = new Header(Header.TYPE_META)
                                .setCmd(Header.CMD_GET)
                                .setLength(buf.readableBytes());

            TransferData td = new TransferData(header, buf);
            ctx.writeAndFlush(td);

        // 파일 데이터
        } else if(msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) msg;
            Header header = new Header(Header.TYPE_DATA)
                                .setCmd(Header.CMD_GET)
                                .setLength(buf.readableBytes());

            TransferData td = new TransferData(header, buf);
            ctx.writeAndFlush(td);

        // 그 외
        } else {
            ctx.writeAndFlush(msg);
        }

    }

}