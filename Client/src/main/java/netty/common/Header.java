package netty.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Header {

    public static final int HEADER_SIZE = 5;
    public static final int CHUNK_SIZE = 32_000; // PC 성능, 메모리 용량에 따라 조절

    public static final int TYPE_SIG = 0x00;
    public static final int TYPE_META = 0x01;
    public static final int TYPE_DATA = 0x02;
    public static final int TYPE_MSG = 0x03;

    public static final int CMD_PUT = 0x00;
    public static final int CMD_GET = 0x01;
    public static final int CMD_FIN = 0x02;

    private static final int TYPE_BIT = 0b1100_0000;
    private static final int CMD_BIT = 0b0010_0000;
    private static final int EOF_BIT = 0b0001_0000;
    private static final int OK_BIT = 0b0000_1000;

    private static final int TYPE_OFFSET = 6;
    private static final int CMD_OFFSET = 5;
    private static final int EOF_OFFSET = 4;
    private static final int OK_OFFSET = 3;

    private int type;
    private int cmd;
    private boolean eof;
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
        this(TYPE_SIG, CMD_PUT, false, true, 0);
    }

    public Header(int type) {
        this.type = type;
    }

    public Header() {
        this(TYPE_SIG, CMD_PUT, false, true, 0);
    }


    public Header(ByteBuf buf) {
        byte data = buf.readByte();
        this.type = (data & TYPE_BIT) >> TYPE_OFFSET;
        this.cmd = (data & CMD_BIT) >> CMD_OFFSET;
        this.eof = ((data & EOF_BIT) >> EOF_OFFSET) == 1;
        this.ok = ((data & OK_BIT) >> OK_OFFSET) == 1;
        this.length = buf.readInt();
        buf.release();
    }

    public ByteBuf toByteBuf() {
        int data = (this.type << TYPE_OFFSET) |
                (this.cmd << CMD_OFFSET) |
                ((this.eof ? 1 : 0) << EOF_OFFSET) |
                ((this.ok ? 1 : 0) << OK_OFFSET);
        return Unpooled.buffer().writeByte(data).writeInt(this.length);
    }

    public Header setHeader(byte data) {
        this.type = (data & TYPE_BIT) >> TYPE_OFFSET;
        this.cmd = (data & CMD_BIT) >> CMD_OFFSET;
        this.eof = ((data & EOF_BIT) >> EOF_OFFSET) == 1;
        this.ok = ((data & OK_BIT) >> OK_OFFSET) == 1;
        return this;
    }

    public boolean isMetadata() {
        return this.type == TYPE_META;
    }

    public boolean isData() {
        return this.type == TYPE_DATA;
    }

    public boolean isMessage() {
        return this.type == TYPE_MSG;
    }

    public boolean isSignal() {
        return this.type == TYPE_SIG;
    }

}
