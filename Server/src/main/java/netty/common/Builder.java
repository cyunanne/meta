package netty.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Builder {

    public static TransferData wrap(FileSpec fs, int cmd) {

        ByteBuf buf = fs.toByteBuf();
        Header header = new Header(Header.TYPE_META)
                .setCmd(cmd)
                .setLength(buf.readableBytes());

        return new TransferData(header, buf);
    }

    public static TransferData wrap(FileSpec fs) {
        return wrap(fs, Header.CMD_GET);
    }

    public static TransferData wrap(ByteBuf data) {

        Header header = new Header(Header.TYPE_DATA);
        header.setLength(data.readableBytes());

        return new TransferData(header, data);
    }

    public static TransferData wrap(String message) {

        Header header = new Header(Header.TYPE_MSG);
        header.setLength(message.length());

        return new TransferData(header, Unpooled.wrappedBuffer(message.getBytes()));
    }

}
