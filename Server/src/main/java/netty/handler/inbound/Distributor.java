package netty.handler.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Distributor extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(Distributor.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("File Channel Connected : " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf byteBuf = td.getData();

        // 파일 정보 수신
        if (header.isMetadata()) {
            FileSpec filespec = new FileSpec(byteBuf);
            String filePath = filespec.getFilePath();

            switch (header.getCmd()) {

                // upload
                case Header.CMD_PUT:
                    ctx.fireChannelRead(msg);
                    break;

                // download
                case Header.CMD_GET:
                    ctx.writeAndFlush(filePath);
                    break;
            }
        }
        
        // 파일 데이터 전달
        else if (header.isData()) {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.info("Channel Closed : " + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }

}