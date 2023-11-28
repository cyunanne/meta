package netty.compressor;

import com.github.luben.zstd.ZstdDirectBufferDecompressingStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledDirectByteBuf;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Decompressor extends ZstdDirectBufferDecompressingStream {

    private ByteBuffer source;
//    private ByteBuf buf;
//    private ByteBuffer bufNio;

    public Decompressor() {
        super(ByteBuffer.allocateDirect(0));

    }

    @Override
    protected ByteBuffer refill(ByteBuffer byteBuffer) {
//        if(byteBuffer.hasRemaining()) {
//            return byteBuffer;
//        }
        return source;
    }

//    public ByteBuf decompress(ByteBuf src) throws IOException {
//        source = src.internalNioBuffer(0, src.readableBytes());
//
//        buf = Unpooled.directBuffer(recommendedTargetBufferSize());
//        bufNio = buf.internalNioBuffer(0, buf.writableBytes());
//
//        int idx = 0;
//        while( (idx = this.read(bufNio)) == 0);
//        buf.writerIndex(idx);
//
//        return buf.retain();
//    }

/*    public int decompress(ByteBuf src, ByteBuf dst) throws IOException {
        source = src.internalNioBuffer(0, src.readableBytes());


//        buf = Unpooled.directBuffer(recommendedTargetBufferSize());
//        bufNio = buf.internalNioBuffer(0, buf.writableBytes());

        ByteBuffer dstNio = dst.internalNioBuffer(0, dst.writableBytes());

        int idx = 0;
        while( (idx = this.read(dstNio)) == 0);
//        dst.writerIndex(idx);
//
////        buf.writerIndex(idx);

        return idx;
    }*/

/*    public int decompress(ByteBuf src, ByteBuffer bufNio) throws IOException {
        source = src.internalNioBuffer(0, src.readableBytes());

        bufNio.put(source);
        return 0;



//        int idx = 0;
//        while( (idx = this.read(bufNio)) == 0 );
//        return idx;
    }*/

    public ByteBuffer decompress(ByteBuf src) throws IOException {
        source = src.nioBuffer(0, src.readableBytes());

        ByteBuffer bufNio = ByteBuffer.allocateDirect(source.limit() * 2);

        int idx = 0;
        while( (idx = this.read(bufNio)) == 0 );

        return bufNio;
    }

    public int decompress(ByteBuf src, ByteBuf buf) throws IOException {
        source = src.nioBuffer(0, src.readableBytes());

        ByteBuffer bufNio = buf.internalNioBuffer(0, buf.writableBytes());
        bufNio.position(0);

        int idx = 0;
        while( (idx = this.read(bufNio)) == 0 );

        buf.writerIndex(bufNio.position());

        return idx;
    }


    /**
     * 잘 되는 소중한 아이
     * @param src
     * @param bufNio
     * @return
     * @throws IOException
     */
    public ByteBuffer decompress(ByteBuf src, ByteBuffer bufNio) throws IOException {
        source = src.nioBuffer(0, src.readableBytes());

        int idx = 0;
        while( (idx = this.read(bufNio)) == 0 );

        return bufNio;
    }

}
