package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;
import java.io.FileOutputStream;

public class FileSaveHandler extends ChannelInboundHandlerAdapter {

    private FileOutputStream fos;

    private Long fileSize = 0L;
    private Long received = 0L;

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

            switch (header.getCmd()) {

                // download
                case Header.CMD_GET:
                    fos = new FileOutputStream(filePath);
                    break;
            }
        }

        // 파일 저장
        if (fos != null) {
            received += fos.getChannel().write(byteBuf.nioBuffer());
            if (received >= fileSize) ctx.close(); // 채널종료
        }
        byteBuf.release();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(fos != null) {
            fos.getChannel().force(true);
            fos.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        cause.printStackTrace();
    }

}