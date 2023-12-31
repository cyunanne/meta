package netty.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class TransferData {

    private Header header;
    private ByteBuf data;

    public TransferData(Header header, ByteBuf byteBuf) {
        this.header = header;
        this.data = byteBuf;
        this.header.setLength(byteBuf.readableBytes());
    }

    public TransferData(Header header, String msg) {
        this(header, Unpooled.wrappedBuffer(msg.getBytes()));
    }

    public TransferData(FileSpec fs) {
        this(new Header(Header.TYPE_META), fs.toByteBuf());
    }

    public ByteBuf getData() {
        return data.resetReaderIndex();
    }

    public TransferData setData(ByteBuf buf) {
        data.release();
        data = buf.retain();
        return this;
    }

    public TransferData setData(byte[] data) {
        return this.setData(Unpooled.wrappedBuffer(data));
    }

    public TransferData setDataAndLength(ByteBuf buf) {
        this.header.setLength(buf.readableBytes());
        return this.setData(buf);
    }

    public TransferData setDataAndLength(byte[] data) {
        return this.setDataAndLength(Unpooled.wrappedBuffer(data));
    }

    public Header getHeader() {
        return header;
    }
}
