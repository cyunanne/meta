package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class FileSaveHandler extends ChannelInboundHandlerAdapter {

    private FileOutputStream fos;

    private long fileSize = 0L;
    private long received = 0L;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws FileNotFoundException {
        System.out.println("File Channel Connected : " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf byteBuf = td.getData();

        // 파일 정보 수신
        if(header.getType() == Header.TYPE_META) {
            FileSpec filespec = new FileSpec(byteBuf.readBytes(header.getLength()));
            fileSize = filespec.getSize();

            String filePath = filespec.getName();
//            filePath += (filespec.isCompressed() ? ".zstd" : "");
//            filePath += (filespec.isEncrypted() ? ".enc" : "");

            switch (header.getCmd()) {

                // upload
                case Header.CMD_PUT:
                    fos = new FileOutputStream(filePath);
                    break;

                // download
                case Header.CMD_GET:
                    ctx.writeAndFlush(filePath);
                    break;
            }
        }

        // 파일 수신
        if(fos != null) {
            received += fos.getChannel().write(byteBuf.nioBuffer());
            if (received >= fileSize) ctx.close();
        }

        byteBuf.release();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(fos != null) fos.close();
        System.out.println("Channel Closed : " + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        cause.printStackTrace();
    }

}