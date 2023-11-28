package netty.handler;

import com.github.luben.zstd.ZstdDirectBufferCompressingStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
    private long fileSize = 0L;
    private long finalLength = 0L;
    private int compressionLevel = 3;

    private Decompressor decomp;
    private ByteBuf buf;
    private ByteBuffer bufNio;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {

        // 메타 데이터
        if(msg instanceof FileSpec) {
            FileSpec fs = (FileSpec) msg;
            fileSize = fs.getSize();
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

        if (doCompress && header.getType() == Header.TYPE_DATA) {
            buf.clear();
            bufNio.clear();

            // 됨..222 이게 젤 빠른듯
            decomp.decompress(data, bufNio);
            buf.writerIndex(bufNio.position());
            finalLength += buf.readableBytes();
            td.setDataAndLength(buf);

            if(finalLength == fileSize) {
                buf.release();
                decomp.setFinalize(true);
            }
        }
        ctx.fireChannelRead(td);

    }

}