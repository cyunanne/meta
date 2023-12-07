package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.common.FileSpec;
import netty.common.FileUtils;
import netty.common.Header;
import netty.common.TransferData;

import java.io.FileOutputStream;
import java.nio.charset.Charset;

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

        /* 메타 데이터 */
        if(msg instanceof FileSpec) {
            FileSpec fs = (FileSpec) msg;
            String filePath = fs.getFilePath();
            FileUtils.mkdir(filePath);

            System.out.println("donwload: " + filePath +
                    " (" + fs.getOriginalFileSize() + " bytes)");

            fos = new FileOutputStream(filePath);
            isFinalFile = fs.isEndOfFileList();

            return;
        }

        /* 일반 데이터 */
        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf byteBuf = td.getData();

        // 메세지
        if (header.isMessage()) {
            String message = byteBuf.toString(Charset.defaultCharset());
            System.out.println("Server: " + message);

        // 파일
        } else if (fos != null && header.isData()) {
            fos.getChannel().write(byteBuf.nioBuffer());

            if( header.isEof() ) {
                fos.close();

                if( isFinalFile ) {
                    ctx.close(); // 채널종료
                }
            }
        }

//        ReferenceCountUtil.release(byteBuf);
//        ReferenceCountUtil.release(msg);
        td.destory();
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