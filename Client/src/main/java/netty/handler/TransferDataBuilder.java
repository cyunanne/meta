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

            int headerCmd = fs.getOriginalFileSize() > 0 ? Header.CMD_PUT : Header.CMD_GET;
            if (fs.isCompressed()) headerCmd = Header.CMD_PUT;
            Header header = new Header(Header.TYPE_META)
                                .setCmd(headerCmd)
                                .setLength(buf.readableBytes());

            TransferData td = new TransferData(header, buf);
            ctx.writeAndFlush(td);
        }

        // 파일 데이터
        else if(msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) msg;

            Header header = new Header(Header.TYPE_DATA);
            header.setLength(buf.readableBytes());
            TransferData td = new TransferData(header, buf);

            ctx.writeAndFlush(td);
        }

    }

}