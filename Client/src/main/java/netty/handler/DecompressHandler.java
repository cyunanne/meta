package netty.handler;

import com.github.luben.zstd.ZstdDirectBufferCompressingStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
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

    long before = 0L;
    long decompressed = 0L;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {

        // 메타 데이터
        if(msg instanceof FileSpec) {
            FileSpec fs = (FileSpec) msg;
            doCompress = fs.isCompressed();

            // init decompressor
            if(doCompress) {
                System.out.println("Decompressing...");

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

            before = header.getLength();
            System.out.println("before: " + before);

            int writableLength = Math.min(header.getLength() * 2, Integer.MAX_VALUE);
            buf.ensureWritable(writableLength);
            bufNio = buf.internalNioBuffer(0, buf.writableBytes());

            decomp.decompress(data, bufNio);
            buf.writerIndex(bufNio.position());
            td.setDataAndLength(buf.retain());

            decompressed += buf.readableBytes();
            System.out.println("after: " + buf.readableBytes());
            System.out.println("decompressed: " + decompressed);

//            if(header.isEof()) {
//                buf.release();
//            }
        }
        ctx.fireChannelRead(td);

    }

}