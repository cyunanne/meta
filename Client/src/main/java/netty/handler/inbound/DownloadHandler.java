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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class DownloadHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(DownloadHandler.class);
    private final FileTransfer transfer;
    private FileOutputStream fos;
    private FileSpec fs;
    private String filePath;
    private long progress = 0L;

    public DownloadHandler(FileTransfer transfer) {
        this.transfer = transfer;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf data = td.getData();

        switch (header.getType()) {

            case Header.TYPE_SIG: break;

            case Header.TYPE_META:
                fs = new FileSpec(data);
                initFileStream();
                break;

            case Header.TYPE_DATA:
                progress += fos.getChannel().write(data.nioBuffer());
                printProgress(progress);
                
                if (progress == fs.getOriginalFileSize() && header.isEof()) { // 파일 끝
                    System.out.println();
                    closeFileStream();
                    ctx.close(); // 채널종료
                }
                break;

            case Header.TYPE_MSG:
                processMessage(ctx, data);
                break;

            default: logger.error("알 수 없는 데이터 타입");
        }

        ReferenceCountUtil.release(msg);
        ReferenceCountUtil.release(data);
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

    private void processMessage(ChannelHandlerContext ctx, ByteBuf data) {
        String message = data.toString(Charset.defaultCharset());

        // 에러 메시지
        if(message.startsWith("error")) {
            System.out.println(message);
            ctx.close();

        // 채널 종료 메시지
        } else if(message.equals("fin")) {
            ctx.close();

        // 파일 목록 수신 -> 다운로드 요청
        } else {
            transfer.download(message);
        }
    }

    private void initFileStream() throws IOException {
        filePath = fs.getFilePath();
        FileUtils.mkdir(filePath); // 경로 내 폴더 생성
        fos = new FileOutputStream(filePath);
    }

    private void closeFileStream() throws IOException {
        fos.close();
        logger.info("Download Complete: " + filePath + " " + fs.getOriginalFileSize() + "bytes");
    }

    private void printProgress(long progress) {
        long percentage = progress == 0 ? 100 : 100*progress/fs.getOriginalFileSize();
        System.out.printf("\r%s : %d / %d bytes (%d %%)", fs.getFilePath(),
                progress, fs.getOriginalFileSize(), percentage);
    }

}