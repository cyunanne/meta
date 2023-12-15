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
    private FileSpec fs;

    @Override
    protected void encode(ChannelHandlerContext ctx, TransferData td, List<Object> list) {
        Header header = td.getHeader();

        if (header.isMetadata()) {
            fs = new FileSpec(td.getData());
            this.fileSize = fs.getOriginalFileSize();

        } else if (header.isData()) {
            transferred += header.getLength();

            if (transferred == fileSize || header.isEof()) {
                printProgress(transferred);

                transferred = 0L;
                header.setEof(true);
            }
        }

        list.add(header.toByteBuf());
        list.add(td.getData());
    }

    private void printProgress(long transferred) {
        System.out.printf("%s 업로드 완료 (%d bytes)\n", fs.getFilePath(), transferred);
    }
}
