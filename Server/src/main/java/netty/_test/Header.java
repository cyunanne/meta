package netty._test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Header {

    public static final int HEADER_SIZE = 3;

    //    public enum TYPE { MSG, META, DATA }
//    public enum CMD { PUT, GET }
    public static final int TYPE_MSG = 0x00;
    public static final int TYPE_META = 0x01;
    public static final int TYPE_DATA = 0x02;
    public static final int CMD_PUT = 0x00;
    public static final int CMD_GET = 0x01;

    private static final int TYPE_BIT = 0b1100_0000;
    private static final int CMD_BIT = 0b0010_0000;
    private static final int EOF_BIT = 0b0001_0000;

    private static final int TYPE_INDEX = 6;
    private static final int CMD_INDEX = 5;
    private static final int EOF_INDEX = 4;

    private final int type; // 0 : message / 1 : meta-data / 2 : data
    private final int cmd; // 0 : put / 1 : get
    private final boolean eof; // true : eof
    private final int length;

    public Header(int type, int cmd, boolean eof, int length) {
        this.type = type;
        this.cmd = cmd;
        this.eof = eof;
        this.length = length;
    }

    public Header(int type, int length) {
        this.type = type;
        this.cmd = CMD_PUT;
        this.eof = true;
        this.length = length;
    }

    public Header(ByteBuf buf) {
        byte data = buf.readByte();
        this.type = (data & TYPE_BIT) >> TYPE_INDEX;
        this.cmd = (data & CMD_BIT) >> CMD_INDEX;
        this.eof = ((data & EOF_BIT) >> EOF_INDEX) == 1;
        this.length = buf.readUnsignedShort();
    }

    public ByteBuf makeHeader() {
        int data = ( this.type << TYPE_INDEX ) |
                ( this.cmd << CMD_INDEX ) |
                ( (this.eof ? 1 : 0) << EOF_INDEX );
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

    public int getLength() {
        return length;
    }

//    public ByteBuf getByteBuf() {
//        ByteBuf buf = Unpooled.buffer();
//        buf.writeShort(this.length);
//        buf.writeBytes(data);
//        list.add(buf);
//    }

}
