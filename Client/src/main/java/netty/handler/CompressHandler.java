package netty.handler;

import com.github.luben.zstd.Zstd;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;

import java.nio.ByteBuffer;

public class CompressHandler extends ChannelOutboundHandlerAdapter {

    private boolean doCompress = false;
    private long compressedLength = 0L;
    private long finalLength = 0L;
    private int compressionLevel = 3;
    private FileSpec fs;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf data = td.getData();

        // 메타 데이터
        if (header.getType() == Header.TYPE_META) {
            fs = new FileSpec(data);
            doCompress = fs.isCompressed();

            if(doCompress) System.out.println("Compressing...");
        }

        // 데이터 압축
        else if(doCompress && header.getType() == Header.TYPE_DATA) {
            int len = header.getLength();
            compressedLength += len;

            ByteBuf buffer = ctx.alloc().directBuffer(len);
            finalLength += compress(data, buffer);
            td.setDataAndLength(buffer);
            buffer.release();

            // 마지막 블록 압축 후 압축 결과 서버에 알리기
            if(fs.getSize() == compressedLength) {
                fs.setSize(finalLength);
                ctx.writeAndFlush(getNewFileSpec());
            }
        }

        ctx.writeAndFlush(td);
    }

    /**
     * 압축
     * @param origin before compression
     * @param target result of compression
     * @return length of compressed data
     */
    private int compress(ByteBuf origin, ByteBuf target) {
        ByteBuffer originNio = origin.internalNioBuffer(0, origin.readableBytes());
        target.writeBytes(Zstd.compress(originNio, compressionLevel));
        return target.readableBytes();
    }

    private TransferData getNewFileSpec() {
        Header header = new Header(Header.TYPE_META);
        fs.setSize(compressedLength);
        return new TransferData(header, fs.toByteBuf());
    }

}
