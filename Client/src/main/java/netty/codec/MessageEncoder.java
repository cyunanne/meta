package netty.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import netty.test.Header;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MessageEncoder extends MessageToMessageEncoder<String> {
    private ByteArrayOutputStream bos = new ByteArrayOutputStream();
    private ObjectOutputStream oos = new ObjectOutputStream(bos);

    public MessageEncoder() throws IOException {}

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, List<Object> list) throws Exception {

        int msgSize = msg.getBytes(StandardCharsets.UTF_8).length;
        Header header = new Header('M', msgSize);
        oos.writeObject(header);

        list.add(bos.toByteArray()); // header(byte[])
        list.add(msg); // msg(String)
    }
}
