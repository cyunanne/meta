package netty._test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class TransferData {
    private final Header header;
    private final ByteBuf data;

    public TransferData(Header header, ByteBuf byteBuf) {
        this.header = header;
        this.data = byteBuf;
    }

    public TransferData(FileSpec fs) {
        this.data = fs.getByteBuf();
        this.header = new Header(Header.TYPE_META, this.data.readableBytes());
    }

    public TransferData(byte[] data, int cmd, boolean eof) {
        this.data = Unpooled.wrappedBuffer(data);
        this.header = new Header(Header.TYPE_DATA, cmd, eof, data.length);
    }

    public TransferData(byte[] data, int cmd, boolean eof, int length) {
        this.data = Unpooled.wrappedBuffer(data, 0, length);
        this.header = new Header(Header.TYPE_DATA, cmd, eof, length);
    }

    public Header getHeader() {
        return header;
    }

    public ByteBuf getData() {
        return data;
    }

}
