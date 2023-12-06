package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import netty.common.FileSpec;
import netty.common.FileUtils;
import netty.common.Header;
import netty.common.TransferData;

import java.io.FileOutputStream;

public class DownloadHandler extends ChannelInboundHandlerAdapter {

    private FileOutputStream fos;
    private boolean isFinalFile = false;

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
            String filePath = fs.getFilePath();
            FileUtils.mkdir(filePath);

            System.out.println("donwload : " + filePath +
                    " (" + fs.getOriginalFileSize() + " bytes)");

            fos = new FileOutputStream(filePath);
            isFinalFile = fs.isEndOfFileList();

            return;
        }

        // 파일 데이터
        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf byteBuf = td.getData();

        if (fos != null) {
            fos.getChannel().write(byteBuf.nioBuffer());

            if( header.isEof() ) {
                fos.close();

                if( isFinalFile ) {
                    ctx.close(); // 채널종료
                }
            }
        }

        ReferenceCountUtil.release(byteBuf);
        ReferenceCountUtil.release(msg);
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