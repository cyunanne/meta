package netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import netty._test.Header;
import netty._test.TransferData;

import java.util.List;

public class Sender extends MessageToMessageEncoder<TransferData> {

    @Override
    protected void encode(ChannelHandlerContext ctx, TransferData dt, List<Object> list) throws Exception {
//        System.out.println("FileEncoderByteBuf.encode()");
        Header header = dt.getHeader();
        list.add(header.makeHeader());
        if(header.getType() == Header.TYPE_MSG) return;
        list.add(dt.getData());
    }
}
