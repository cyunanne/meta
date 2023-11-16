package netty.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;

public class Header {

    public static final int HEADER_SIZE = 3;

    public static final int TYPE_MSG = 0x00;
    public static final int TYPE_META = 0x01;
    public static final int TYPE_DATA = 0x02;

    public static final int CMD_PUT = 0x00;
    public static final int CMD_GET = 0x01;

    private static final int TYPE_BIT = 0b1100_0000;
    private static final int CMD_BIT = 0b0010_0000;
    private static final int EOF_BIT = 0b0001_0000;
    private static final int OK_BIT = 0b0000_1000;

    private static final int TYPE_INDEX = 6;
    private static final int CMD_INDEX = 5;
    private static final int EOF_INDEX = 4;
    private static final int OK_INDEX = 3;

    private int type; // 0 : message / 1 : meta-data / 2 : data
    private int cmd; // 0 : put / 1 : get
    private boolean eof; // true : eof
    private boolean ok;
    private int length;

    public Header(int type, int cmd, boolean eof, boolean ok, int length) {
        this.type = type;
        this.cmd = cmd;
        this.eof = eof;
        this.ok = ok;
        this.length = length;
    }

    public Header(int type, int cmd, boolean eof, int length) {
        this(type, cmd, eof, true, length);
    }

    public Header(int type, int cmd, boolean eof) {
        this(type, cmd, eof, true, 0);
    }


    public Header(int type, int length) {
        this(type, CMD_PUT, false, length);
    }

    public Header(boolean ok) {
        this(TYPE_MSG, CMD_PUT, false, true, 0);
    }

    public Header() {
        this(TYPE_MSG, CMD_PUT, false, true, 0);
    }


    public Header(ByteBuf buf) {
        byte data = buf.readByte();
        this.type = (data & TYPE_BIT) >> TYPE_INDEX;
        this.cmd = (data & CMD_BIT) >> CMD_INDEX;
        this.eof = ((data & EOF_BIT) >> EOF_INDEX) == 1;
        this.ok = ((data & OK_BIT) >> OK_INDEX) == 1;
        this.length = buf.readUnsignedShort();
    }

    public ByteBuf getByteBuf() {
        int data = ( this.type << TYPE_INDEX ) |
                ( this.cmd << CMD_INDEX ) |
                ( (this.eof ? 1 : 0) << EOF_INDEX ) |
                ( (this.ok ? 1 : 0) << OK_INDEX );
        return Unpooled.buffer().writeByte(data).writeShort(this.length);
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

    public Header setHeader(byte data) {
        this.type = (data & TYPE_BIT) >> TYPE_INDEX;
        this.cmd = (data & CMD_BIT) >> CMD_INDEX;
        this.eof = ((data & EOF_BIT) >> EOF_INDEX) == 1;
        this.ok = ((data & OK_BIT) >> OK_INDEX) == 1;
        return this;
    }

    public Header setLength(int length) {
        this.length = length;
        return this;
    }

}
