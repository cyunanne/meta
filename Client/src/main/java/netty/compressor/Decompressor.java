package netty.compressor;

import com.github.luben.zstd.ZstdDirectBufferDecompressingStream;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Decompressor extends ZstdDirectBufferDecompressingStream {

    private ByteBuffer source;

    public Decompressor() {
        super(ByteBuffer.allocateDirect(0));
    }

    @Override
    protected ByteBuffer refill(ByteBuffer byteBuffer) {
        return source;
    }

    public int decompress(ByteBuf src, ByteBuffer bufNio) throws IOException {
        source = src.internalNioBuffer(0, src.readableBytes());

        int idx = 0;
        while( (idx = this.read(bufNio)) == 0 );
        return idx;
    }

}
