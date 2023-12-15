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
    private FileSpec fs;
    private String filePath;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("File Channel Connected : " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf byteBuf = td.getData();

        switch (header.getType()) {

            case Header.TYPE_META:
                fs = new FileSpec(byteBuf);
                filePath = fs.getFilePath();

                if(header.getCmd() == Header.CMD_GET) {
                    ctx.writeAndFlush(filePath); // download
                    return; // channel read 연쇄 중단
                }
                break;

            case Header.TYPE_SIG: break;
            case Header.TYPE_DATA: break;
            case Header.TYPE_MSG: break;

            default: logger.error("알 수 없는 데이터 타입");
        }

        ctx.fireChannelRead(msg);
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