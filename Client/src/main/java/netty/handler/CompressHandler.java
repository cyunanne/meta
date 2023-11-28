package netty.handler;

import com.github.luben.zstd.ZstdDirectBufferCompressingStream;
import io.netty.buffer.ByteBuf;
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
    private long compressedLength = 0L;
    private long finalLength = 0L;
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
        if (header.getType() == Header.TYPE_META) {
            fs = new FileSpec(data);
            doCompress = fs.isCompressed();

            // init compressor
            if(doCompress) {
                System.out.println("Compressing...");

                int bufferSize = ZstdDirectBufferCompressingStream.recommendedOutputBufferSize();
                buf = ctx.alloc().directBuffer(bufferSize);
                bufNio = buf.internalNioBuffer(0, buf.writableBytes());

                comp = new Compressor(bufNio, compressionLevel);
            }
        }

        // 데이터 압축
        else if(doCompress && header.getType() == Header.TYPE_DATA) {
            int len = header.getLength();
            compressedLength += len;

            buf.clear();
            bufNio.clear();

            comp.compress(data);
            buf.writerIndex(bufNio.position());
            td.setDataAndLength(buf);
            finalLength += header.getLength();

            // 마지막 블록 압축 후 압축 결과 서버에 알리기
            if(fs.getSize() == compressedLength) {
                buf.release();
                fs.setSize(finalLength);
                ctx.writeAndFlush(new TransferData(fs));
            }
        }

        ctx.writeAndFlush(td);
    }

}
