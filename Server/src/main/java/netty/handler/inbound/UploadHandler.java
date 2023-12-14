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

        // 파일 정보 수신
        if (header.isMetadata()) {
            fs = new FileSpec(byteBuf);
            String filePath = FileUtils.rename(fs.getFilePath());
            FileUtils.mkdir(filePath);

            if (fos == null) {
                fos = new FileOutputStream(filePath, false);
            }

            // 메타 데이터 저장
            oos = new ObjectOutputStream(fos);
            oos.writeObject(fs);
        }

        // 파일 수신
        if (fos != null) {
            fos.getChannel().write(byteBuf.nioBuffer());
            if(header.isEof()) {
                ctx.close(); // 채널종료
                if(oos != null) oos.close();
                if(fos != null) fos.close();
                if(fs != null) logger.info(String.format("file uploaded: %s (%d bytes)", fs.getFilePath(), FileUtils.getSize(fs.getFilePath())));
            }
        }

        ReferenceCountUtil.release(byteBuf);
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        cause.printStackTrace();
    }

}