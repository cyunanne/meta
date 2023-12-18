package netty.handler.inbound;

import com.github.luben.zstd.ZstdDirectBufferDecompressingStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;
import netty.compressor.Decompressor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;

public class DecompressHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(DecompressHandler.class);
    private boolean doCompress = false;
    private Decompressor decomp;
    private ByteBuf buf;
    private ByteBuffer bufNio;
    private FileSpec fs;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf data = td.getData();

        switch (header.getType()) {

            case Header.TYPE_META:
                fs = new FileSpec(data);
                doCompress = fs.isCompress();
                if (doCompress) initDecompressor();
                break;

            case Header.TYPE_DATA:
                if (!doCompress) break;
                decompress(ctx, td);
                if (header.isEof()) {
                    clearProperties();
                }
                return; // switch 밖 channel read 스킵

            case Header.TYPE_SIG: break;
            case Header.TYPE_MSG: break;

            default: logger.error("알 수 없는 데이터 타입");
        }

        ctx.fireChannelRead(td);
    }

    private void initDecompressor() {
        int bufferSize = ZstdDirectBufferDecompressingStream.recommendedTargetBufferSize();
        buf = Unpooled.directBuffer(bufferSize * 2);
        bufNio = buf.internalNioBuffer(0, buf.writableBytes());
        decomp = new Decompressor();

        logger.info("Decompressor has been created: " + fs.getFilePath());
        logger.info("Decompression has been Started: " + fs.getFilePath()); // 위치 이동 예정
    }

    private void decompress(ChannelHandlerContext ctx, TransferData td) throws IOException {
        bufNio = buf.clear().internalNioBuffer(0, buf.writableBytes());

        decomp.setBuffer(td.getData());
        decomp.decompress(bufNio); // 0byte 파일 처리
        do {
            buf.writerIndex(bufNio.position());
            td.setDataAndLength(buf.retain());
            ctx.fireChannelRead(td);
        } while (decomp.decompress(bufNio));
    }

    private void clearProperties() throws IOException {
        decomp.close();
        doCompress = false;
        ReferenceCountUtil.release(buf);

        logger.info("Decompression finished: " + fs.getFilePath());
    }

}