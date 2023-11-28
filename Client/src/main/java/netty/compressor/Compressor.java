package netty.compressor;

import com.github.luben.zstd.ZstdDirectBufferCompressingStream;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Compressor extends ZstdDirectBufferCompressingStream {

    public Compressor(ByteBuffer byteBuffer, int i) throws IOException {
        super(byteBuffer, i);
    }

    public void compress(ByteBuf origin) throws IOException {
        this.compress(origin.nioBuffer(0, origin.readableBytes()));
        this.flush();
    }

}
