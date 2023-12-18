package netty.handler.outbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class SenderForServer extends MessageToMessageEncoder<TransferData> {

    private static final Logger logger = LogManager.getLogger(SenderForServer.class);
    private long transferred = 0L;
    private long fileSize = 0L;

    @Override
    protected void encode(ChannelHandlerContext ctx, TransferData td, List<Object> list) {
        Header header = td.getHeader();
        transferred += header.getLength();

        switch (header.getType()) {
            case Header.TYPE_META:
                FileSpec fs = new FileSpec(td.getData());
                this.fileSize = fs.getCurrentFileSize();
                break;

            case Header.TYPE_DATA:
                if (transferred == fileSize) {
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
}
