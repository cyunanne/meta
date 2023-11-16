package netty.common;

import io.netty.buffer.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.*;

public class ChunkedFileWithH implements ChunkedInput<ByteBuf> {
    private final ChunkedInput<ByteBuf> chunkedInput;
    private boolean headerAppended;
    private final ByteBuf header;

    public ChunkedFileWithH(ChunkedInput<ByteBuf> chunkedInput, Header headerData) {
        this.chunkedInput = chunkedInput;
        this.header = headerData.getByteBuf();
    }

    @Override
    public boolean isEndOfInput() throws Exception {
        return headerAppended && chunkedInput.isEndOfInput();
    }

    @Override
    public void close() throws Exception {
        chunkedInput.close();
    }

    @Override
    public ByteBuf readChunk(ChannelHandlerContext ctx) throws Exception {
        if (!headerAppended) {
            headerAppended = true;
            return header.retain();
        }
        return chunkedInput.readChunk(ctx);
    }

    @Override
    public ByteBuf readChunk(ByteBufAllocator byteBufAllocator) {
        return null;
    }

    @Override
    public long length() {
        return chunkedInput.length();
    }

    @Override
    public long progress() {
        return chunkedInput.progress();
    }

}