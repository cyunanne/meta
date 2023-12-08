package netty.handler;

import com.github.luben.zstd.ZstdDirectBufferCompressingStream;
import io.netty.buffer.ByteBuf;
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
                buf = ctx.alloc().directBuffer(bufferSize);
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

            int writableLength = Math.min(header.getLength() * 2, Integer.MAX_VALUE);
            buf.ensureWritable(writableLength);
            ByteBuffer bufNio = buf.internalNioBuffer(0, buf.writableBytes());

            int idx = decomp.decompress(data, bufNio);
            buf.writerIndex(idx);
            td.setDataAndLength(buf.duplicate());

            if(header.isEof()) {
                buf.release();
                decomp.setFinalize(true);
            }
        }
        ctx.fireChannelRead(td);

    }

}