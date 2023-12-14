package netty.handler.outbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;

import java.util.List;

public class Sender extends MessageToMessageEncoder<TransferData> {

    private static int total = 0;
    private static int finished = 0;
    private long transferred = 0L;
    private long fileSize = 0L;
    private FileSpec fs;

    @Override
    protected void encode(ChannelHandlerContext ctx, TransferData td, List<Object> list) {
        Header header = td.getHeader();

        if (header.isMetadata()) {
            fs = new FileSpec(td.getData());
            this.fileSize = fs.getOriginalFileSize();
            total++;

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
        System.out.printf("[%d/%d] %s 업로드 완료 (%d bytes)\n",
                ++finished, total, fs.getFilePath(), transferred);
        if(finished == total) finished = total = 0;
    }
}
