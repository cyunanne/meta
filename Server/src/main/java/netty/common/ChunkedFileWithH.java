package netty.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedInput;
import io.netty.util.ReferenceCountUtil;

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
            return header.retainedDuplicate(); // 헤더의 retained duplicate를 반환하여 참조 카운트 증가
        }

        ByteBuf chunk = chunkedInput.readChunk(ctx);

        if (chunk != null) {
            ReferenceCountUtil.retain(chunk); // 실제 데이터를 읽은 후 참조 카운트 증가
        }

        return chunk;
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