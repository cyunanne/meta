package netty.compressor;

import com.github.luben.zstd.ZstdDirectBufferCompressingStream;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Compressor extends ZstdDirectBufferCompressingStream {

    public Compressor(ByteBuffer byteBuffer, int compressionLevel) throws IOException {
        super(byteBuffer, compressionLevel);
    }

    public void compress(ByteBuf src) throws IOException {
        this.compress(src.nioBuffer(0, src.readableBytes()));
        this.flush();
    }

}
