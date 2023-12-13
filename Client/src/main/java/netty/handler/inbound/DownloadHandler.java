package netty.handler.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import netty.FileTransfer;
import netty.common.FileSpec;
import netty.common.FileUtils;
import netty.common.Header;
import netty.common.TransferData;

import java.io.FileOutputStream;
import java.nio.charset.Charset;

public class DownloadHandler extends ChannelInboundHandlerAdapter {

    private FileTransfer transfer;
    private FileOutputStream fos;
    private FileSpec fs;
    private String filePath;

    public DownloadHandler(FileTransfer transfer) {
        this.transfer = transfer;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        /* 1. 메타 데이터 */
        if(msg instanceof FileSpec) {
            fs = (FileSpec) msg;
            filePath = fs.getFilePath();
            FileUtils.mkdir(filePath);
            fos = new FileOutputStream(filePath);
            return;
        }

        /* 2. 일반 데이터 */
        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf byteBuf = td.getData();

        // 2-1) 메세지
        if (header.isMessage()) {
            String message = byteBuf.toString(Charset.defaultCharset());

            // 에러 메시지
            if(message.startsWith("error")) {
                System.out.println(message);
                ctx.close();

            // 채널 종료 메시지
            } else if(message.equals("fin")) {
                ctx.close();

            // 파일 목록
            } else {
//                new FileTransfer("localhost", 8889).download(message);
                transfer.download(message);
            }

        // 2-2) 파일 데이터
        } else if (header.isData() && fos != null) {
            fos.getChannel().write(byteBuf.nioBuffer());

            if (header.isEof()) {
                fos.close();
                ctx.close(); // 채널종료

                System.out.printf("다운로드 성공: %s (%d bytes)%n", filePath, fs.getOriginalFileSize());
            }

        // 2-3) signal
//        } else if (header.isSignal()) {
//            if(header.isFin()) {
//                ctx.close();
//            }
        }

        ReferenceCountUtil.release(byteBuf);
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if(fos != null) fos.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        cause.printStackTrace();
    }

}