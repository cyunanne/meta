package netty.handler.outbound;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;
import netty.compressor.Compressor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;

public class CompressHandler extends ChannelOutboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(CompressHandler.class);
    private boolean doCompress = false;
    private long compressed = 0L;
    private int compressionLevel = 3;
    private FileSpec fs;

    private Compressor comp;
    private ByteBuf buf;
    private ByteBuffer bufNio;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf data = td.getData();

        // 메타 데이터 확인 -> compressor 초기화
        if (header.isMetadata()) {
            fs = new FileSpec(data);
            doCompress = fs.isCompressed();

            // init compressor
            if (doCompress) {
                logger.info("Compressing...: " + fs.getFilePath());

                int writableLength = Math.min(Header.CHUNK_SIZE * 2, Integer.MAX_VALUE);
                buf = Unpooled.directBuffer(writableLength);
                bufNio = buf.internalNioBuffer(0, buf.writableBytes());
                comp = new Compressor(bufNio, compressionLevel);
            }
        }

        // 데이터 압축
        else if (doCompress && header.isData()) {
            int len = header.getLength();
            compressed += len;

            buf.clear();
            bufNio.clear();

            comp.compress(data);

            buf.writerIndex(bufNio.position());
            td.setDataAndLength(buf.duplicate());

            // 마지막 블록 eof 설정
            if (fs.getOriginalFileSize() == compressed) {
                header.setEof(true);
                clearVariables();

                logger.info("Compressing Finished: " + fs.getFilePath());
            }
        }

        ctx.writeAndFlush(td);
    }

    private void clearVariables() throws IOException {
        comp.setFinalize(true);
        comp.close();
        compressed = 0L;
        ReferenceCountUtil.release(buf);
    }

}
