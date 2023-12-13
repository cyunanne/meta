package netty.handler.inbound;

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

        // 메타 데이터
        if(msg instanceof FileSpec) {
            fs = (FileSpec) msg;
            doCompress = fs.isCompressed();

            // init decompressor
            if(doCompress && decomp == null) {
                logger.info("Decompressing...: " + fs.getFilePath());

                buf = Unpooled.directBuffer(Header.CHUNK_SIZE);
                bufNio = buf.internalNioBuffer(0, buf.writableBytes());
                decomp = new Decompressor();
            }

            ctx.fireChannelRead(fs);
            return;
        }

        // 파일 데이터 압축 해제
        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf data = td.getData();

        if (doCompress && header.isData()) {

            buf.clear();
            bufNio.clear();

            int writableLength = Math.min(Header.CHUNK_SIZE * 2, Integer.MAX_VALUE);
            buf.ensureWritable(writableLength);
            bufNio = buf.internalNioBuffer(0, buf.writableBytes());

            decomp.setBuffer(data);
            decomp.decompress(bufNio);
            do {
                buf.writerIndex(bufNio.position());
                td.setDataAndLength(buf.retain());
                ctx.fireChannelRead(td);
            } while (decomp.decompress(bufNio));

            if (header.isEof()) {
                clearVariables();
                logger.info("Decompressing Finished: " + fs.getFilePath());
            }
        }

        else {
            ctx.fireChannelRead(td);
        }
    }

    private void clearVariables() throws IOException {
        decomp.close();
        ReferenceCountUtil.release(buf);
    }
}