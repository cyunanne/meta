package netty.handler;

import com.github.luben.zstd.ZstdDirectBufferCompressingStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;
import netty.compressor.Decompressor;

import java.io.IOException;
import java.nio.ByteBuffer;

public class DecompressHandler extends ChannelInboundHandlerAdapter {

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
                System.out.println("Decompressing...: " + fs.getFilePath());

                int bufferSize = ZstdDirectBufferCompressingStream.recommendedOutputBufferSize() * 2;
                buf = Unpooled.directBuffer(bufferSize);
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

//            int writableLength = Math.min(header.getLength() * 2, Integer.MAX_VALUE);
//            buf.ensureWritable(writableLength);
            buf.ensureWritable(Header.CHUNK_SIZE);
            bufNio = buf.internalNioBuffer(0, buf.writableBytes());

            decomp.setBuffer(data);
            while( decomp.decompress(bufNio) ) {

                buf.writerIndex(bufNio.position());
                td.setDataAndLength(buf.retain());
                ctx.fireChannelRead(td);

            }

            if(header.isEof()) {
                System.out.println("Deompressing Finished: " + fs.getFilePath());
                clearVariables();
            }
        }

        else {
            ctx.fireChannelRead(td);
        }

    }

    private void clearVariables() throws IOException {
        decomp.close();
        decomp = null;
        ReferenceCountUtil.release(buf);
    }
}