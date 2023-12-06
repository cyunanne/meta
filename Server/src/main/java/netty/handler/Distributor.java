package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;

import java.io.FileOutputStream;

public class Distributor extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("File Channel Connected : " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf byteBuf = td.getData();

        // 파일 정보 수신
        if(header.getType() == Header.TYPE_META) {
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
        else if(header.getType() == Header.TYPE_DATA) {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Channel Closed : " + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }

}