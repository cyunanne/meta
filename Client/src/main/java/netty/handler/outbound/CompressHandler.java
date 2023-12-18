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

        switch (header.getType()) {

            case Header.TYPE_META:
                fs = new FileSpec(data);
                doCompress = fs.isCompress();
                if (doCompress) initCompressor();
                break;

            case Header.TYPE_DATA:
                if (!doCompress) break;
                compress(header, data);
                td.setDataAndLength(buf.duplicate());
                if (header.isEof()) clearProperties();
                break;

            case Header.TYPE_SIG: break;
            case Header.TYPE_MSG: break;

            default: logger.error("알 수 없는 데이터 타입");
        }

        ctx.writeAndFlush(td);
    }

    private void initCompressor() throws IOException {
        int writableLength = Math.min(Header.CHUNK_SIZE * 2, Integer.MAX_VALUE);
        buf = Unpooled.directBuffer(writableLength);
        bufNio = buf.internalNioBuffer(0, buf.writableBytes());
        comp = new Compressor(bufNio, compressionLevel);

        logger.debug("Compressor has been created: " + fs.getFilePath());
        logger.info("Compression has been Started: " + fs.getFilePath()); // 위치 이동 예정
    }

    private void compress(Header header, ByteBuf data) throws IOException {
        buf.clear();
        bufNio.clear();
        comp.compress(data);
        buf.writerIndex(bufNio.position());

        // 파일 끝 설정
        compressed += header.getLength();
        if (fs.getOriginalFileSize() == compressed) {
            header.setEof(true);
        }
    }

    private void clearProperties() throws IOException {
        comp.setFinalize(true);
        comp.close();
        compressed = 0L;
        ReferenceCountUtil.release(buf);

        logger.info("Compressing Finished: " + fs.getFilePath());
    }

}
