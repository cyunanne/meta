package netty.compressor;

import com.github.luben.zstd.ZstdDirectBufferDecompressingStream;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Decompressor extends ZstdDirectBufferDecompressingStream {

    private ByteBuffer buffer;
    private boolean isFirstBlock = true;

    public Decompressor() {
        super(ByteBuffer.allocateDirect(0));
    }

    @Override
    protected ByteBuffer refill(ByteBuffer byteBuffer) {
        return byteBuffer.hasRemaining() ? byteBuffer : buffer;
    }

    public boolean decompress(ByteBuffer bufNio) throws IOException {

        if(isFirstBlock) {
            isFirstBlock = false;
            this.read(bufNio);
        }

        bufNio.clear();
        return this.read(bufNio) != 0;
    }

    public void setBuffer(ByteBuf src) {
        isFirstBlock = true;
        buffer = src.internalNioBuffer(0, src.readableBytes());
    }

}
