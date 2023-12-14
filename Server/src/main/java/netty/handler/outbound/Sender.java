package netty.handler.outbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;

import java.util.List;

public class Sender extends MessageToMessageEncoder<TransferData> {

    private long transferred = 0L;
    private long fileSize = 0L;

    @Override
    protected void encode(ChannelHandlerContext ctx, TransferData td, List<Object> list) {
        Header header = td.getHeader();
        transferred += header.getLength();

        if (header.isMetadata()) {
            FileSpec fs = new FileSpec(td.getData());
            this.fileSize = fs.getCurrentFileSize();

        } else if (header.isData()) {
            if (transferred == fileSize) {
                header.setEof(true);
                transferred = 0L;
            }
        }

        list.add(header.toByteBuf());
        list.add(td.getData());
    }
}
