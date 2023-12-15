package netty.handler.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import netty.common.FileSpec;
import netty.common.FileUtils;
import netty.common.Header;
import netty.common.TransferData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class UploadHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(UploadHandler.class);
    private FileOutputStream fos;
    private ObjectOutputStream oos;
    private FileSpec fs;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf byteBuf = td.getData();

        switch (header.getType()) {

            case Header.TYPE_META:
                fs = new FileSpec(byteBuf);
                writeMetaData();
                break;

            case Header.TYPE_DATA:
                if(fos == null) break;
                fos.getChannel().write(byteBuf.nioBuffer());
                if(header.isEof()) ctx.close(); // 채널종료
                break;

            case Header.TYPE_SIG: break;
            case Header.TYPE_MSG: break;

            default: logger.error("알 수 없는 데이터 타입");
        }

        ReferenceCountUtil.release(byteBuf);
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        closeFileStream();
        if(fs != null) {
            logger.info(String.format("file uploaded: %s (%d bytes)",
                    fs.getFilePath(), FileUtils.getSize(fs.getFilePath())));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        cause.printStackTrace();
    }

    private void writeMetaData() throws IOException {
        openFileStream(fs.getFilePath());
        oos = new ObjectOutputStream(fos);
        oos.writeObject(fs);
    }

    private void openFileStream(String path) throws IOException {
        String filePath = FileUtils.rename(path);
        FileUtils.mkdir(filePath);
        fos = new FileOutputStream(filePath, false);
    }

    private void closeFileStream() throws IOException {
        if(oos != null) oos.close();
        if(fos != null) fos.close();
    }

}