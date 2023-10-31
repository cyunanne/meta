package netty.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import netty.test.Header;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FileEncoder extends MessageToMessageEncoder<byte[]> {
    private ByteArrayOutputStream bos = new ByteArrayOutputStream();
    private ObjectOutputStream oos = new ObjectOutputStream(bos);

    public FileEncoder() throws IOException {}

    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] file, List<Object> list) throws Exception {

        int msgSize = file.length;
        Header header = new Header('F', msgSize);
        oos.writeObject(header);

        list.add(bos.toByteArray()); // header(byte[])
        list.add(file); // file(byte[])
    }
}
