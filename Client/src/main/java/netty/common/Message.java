package netty.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Message {

    public static final int HEADER_SIZE = 3;

    public static final int TYPE_SIGNAL = 0;
    public static final int TYPE_HEADER = 1;

    public static final int CMD_PUT = 0; // upload
    public static final int CMD_GET = 1; // download

    private static final int TYPE_BIT = 0b1000_0000;
    private static final int CMD_BIT  = 0b0100_0000;
    private static final int OK_BIT   = 0b0010_0000;
    private static final int EOF_BIT  = 0b0001_0000;

    private static final int TYPE_INDEX = 7;
    private static final int CMD_INDEX = 6;
    private static final int OK_INDEX = 5;
    private static final int EOF_INDEX = 4;

    private int type; // 0 : message / 1 : meta-data
    private int cmd; // 0 : put / 1 : get
    private boolean ok;
    private boolean eof; // true : eof
    private int length;

    private ByteBuf data;

    public Message(int type, int cmd, boolean ok, boolean eof) {
        this.type = type;
        this.cmd = cmd;
        this.ok = ok;
        this.eof = eof;
    }

    public Message(int type, int cmd, boolean eof) {
        this(type, cmd, eof, true);
    }

    public Message(int type, int cmd) {
        this(type, cmd, false);
    }

    public Message(int cmd) {
        this(TYPE_HEADER, cmd, false, false);
    }

    public Message(boolean ok) {
        this(TYPE_SIGNAL, CMD_PUT, true, false);
    }

    public Message(ByteBuf buf) {
        byte data = buf.readByte();
        this.type = (data & TYPE_BIT) >> TYPE_INDEX;
        this.cmd = (data & CMD_BIT) >> CMD_INDEX;
        this.ok = ((data & OK_BIT) >> OK_INDEX) == 1;
        this.eof = ((data & EOF_BIT) >> EOF_INDEX) == 1;
        this.length = buf.readUnsignedShort();
    }

    public Message(byte data) {
        this.type = (data & TYPE_BIT) >> TYPE_INDEX;
        this.cmd = (data & CMD_BIT) >> CMD_INDEX;
        this.ok = ((data & OK_BIT) >> OK_INDEX) == 1;
        this.eof = ((data & EOF_BIT) >> EOF_INDEX) == 1;
    }

    public ByteBuf getByteBuf() {
        int header = ( this.type << TYPE_INDEX ) |
                ( this.cmd << CMD_INDEX ) |
                ( (this.ok ? 1 : 0) << OK_INDEX ) |
                ( (this.eof ? 1 : 0) << EOF_INDEX );

        if(data == null)
            return Unpooled.buffer()
                    .writeByte(header)
                    .writeShort(this.length);
        else
            return Unpooled.buffer()
                    .writeByte(header)
                    .writeShort(this.length)
                    .writeBytes(data);
    }

    public int getType() {
        return type;
    }

    public int getCmd() {
        return cmd;
    }

    public boolean isEof() {
        return eof;
    }

    public boolean isOk() {
        return ok;
    }

    public int getLength() {
        return length;
    }

    public Message setHeader(byte data) {
        this.type = (data & TYPE_BIT) >> TYPE_INDEX;
        this.cmd = (data & CMD_BIT) >> CMD_INDEX;
        this.ok = ((data & OK_BIT) >> OK_INDEX) == 1;
        this.eof = ((data & EOF_BIT) >> EOF_INDEX) == 1;
        return this;
    }

    public int setLength(int length) {
        this.length = length;
        return this.length;
    }

    public Message setData(ByteBuf data) {
        this.data = data;
        this.length = data.readableBytes();
        return this;
    }

    public Message setData(String str) {
        this.data = Unpooled.copiedBuffer(str.getBytes());
        this.length = data.readableBytes();
        return this;
    }

    public boolean isSignal() {
        return this.type == TYPE_SIGNAL;
    }

    public boolean isHeader() {
        return this.type == TYPE_HEADER;
    }

    public ByteBuf getData() {
        return data;
    }
}
