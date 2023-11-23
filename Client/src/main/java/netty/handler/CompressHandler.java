package netty.handler;

import com.github.luben.zstd.Zstd;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
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

            if(doCompress) System.out.println("Compress Started.");
        }

        // 데이터 압축
        else if(doCompress && header.getType() == Header.TYPE_DATA) {
            // TODO 데이터 압축 & TransferData 갱신

            compressedLength += header.getLength();
            td.setDataAndLength(compress(data));

            // 압축 결과 파일 크기 서버에 알리기
            if(fs.getSize() == compressedLength) {
                fs.setSize(finalLength);
                ctx.writeAndFlush(getNewFileSpec());
            }
        }

        ctx.writeAndFlush(td);
    }

    private ByteBuf compress(ByteBuf origin) {
        return Unpooled.wrappedBuffer(Zstd.compress(origin.nioBuffer(), origin.readableBytes()));
    }

    private TransferData getNewFileSpec() {
        Header header = new Header(Header.TYPE_META);
        fs.setSize(compressedLength);
        return new TransferData(header, fs.toByteBuf());
    }

}
