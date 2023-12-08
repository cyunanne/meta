package netty.handler;

import com.github.luben.zstd.ZstdDirectBufferCompressingStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;
import netty.compressor.Compressor;

import java.nio.ByteBuffer;

public class CompressHandler extends ChannelOutboundHandlerAdapter {

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

        // 메타 데이터
        if (header.isMetadata()) {
            fs = new FileSpec(data);
            doCompress = fs.isCompressed();

            // init compressor
            if(doCompress && comp == null) {
                System.out.println("Compressing...");

                int bufferSize = ZstdDirectBufferCompressingStream.recommendedOutputBufferSize();
                buf = Unpooled.directBuffer(bufferSize);
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

            // 마지막 블록 압축 후 압축 결과 서버에 알리기 -> 마지막 블록 eof 설정
            if(fs.getOriginalFileSize() == compressed) {
                header.setEof(true);
                compressed = 0L;
            }
        }

        ctx.writeAndFlush(td);
    }

}
