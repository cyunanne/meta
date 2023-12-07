package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TransferDataBuilder extends ChannelOutboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(TransferDataBuilder.class);

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

        // 메시지
        } else if(msg instanceof String){
            String message = (String) msg;
            Header header = new Header(Header.TYPE_MSG)
                                .setCmd(Header.CMD_GET)
                                .setLength(message.length());

            TransferData td = new TransferData(header, message);
            ctx.writeAndFlush(td);

        } else {
            logger.warn("알 수 없는 데이터 타입");
        }

    }

}