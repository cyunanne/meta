package netty.handler.outbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class SenderForClient extends MessageToMessageEncoder<TransferData> {

    private static final Logger logger = LogManager.getLogger(SenderForClient.class);
    private long transferred = 0L;
    private long fileSize = 0L;
    private FileSpec fs;

    @Override
    protected void encode(ChannelHandlerContext ctx, TransferData td, List<Object> list) {
        Header header = td.getHeader();

        switch (header.getType()) {
            case Header.TYPE_META:
                fs = new FileSpec(td.getData());
                fileSize = fs.getOriginalFileSize();
                break;

            case Header.TYPE_DATA:
                transferred += header.getLength();
                if (transferred == fileSize || header.isEof()) {
                    printProgress(transferred);
                    header.setEof(true);
                    transferred = 0L;
                }
                break;

            case Header.TYPE_SIG: break;
            case Header.TYPE_MSG: break;

            default: logger.error("알 수 없는 데이터 타입");
        }

        list.add(header.toByteBuf());
        list.add(td.getData());
    }

    private void printProgress(long transferred) {
        System.out.printf("%s 업로드 완료 (%d bytes)\n", fs.getFilePath(), transferred);
    }
}
