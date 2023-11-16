package netty.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class TransferData {
    private final Header header;
    private ByteBuf data;

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

    public TransferData(int type, int cmd, boolean eof) {
        this.header = new Header(type, cmd, eof, 0);
    }

    public Header getHeader() {
        return header;
    }

    public ByteBuf getData() {
        return data;
    }

    public void delete() {
        data.release();
    }

}
