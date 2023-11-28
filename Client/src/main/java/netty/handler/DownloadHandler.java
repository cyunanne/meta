package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;

import java.io.FileOutputStream;

public class DownloadHandler extends ChannelInboundHandlerAdapter {

    private FileOutputStream fos;
    private long fileSize = 0L;
    private long received = 0L;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("Channel Connected.");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 메타 데이터
        if(msg instanceof FileSpec) {
            FileSpec fs = (FileSpec) msg;
            fileSize = fs.getSize();
            fos = new FileOutputStream(fs.getName());
            return;
        }

        // 파일 데이터
        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf byteBuf = td.getData();

        if (fos != null) {
            received += fos.getChannel().write(byteBuf.nioBuffer());

            if (received == fileSize || header.isEof()) {
                ctx.close(); // 채널종료
            }
        }

        byteBuf.release();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(fos != null) {
            fos.getChannel().close();
            fos.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        cause.printStackTrace();
    }

}