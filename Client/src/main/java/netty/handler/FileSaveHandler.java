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
    private long fileSize = 0L;
    private long received = 0L;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 메타데이터
        if(msg instanceof FileSpec) {
            FileSpec fs = (FileSpec) msg;
            fileSize = fs.getSize();
            fos = new FileOutputStream(fs.getName());
            return;
        }

        // 파일 저장
        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf byteBuf = td.getData();

        if (fos != null) {
            fos.getChannel().write(byteBuf.nioBuffer());
            received += header.getLength();
            if (received == fileSize) ctx.close(); // 채널종료
        }

        byteBuf.release();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(fos != null) fos.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        cause.printStackTrace();
    }

}