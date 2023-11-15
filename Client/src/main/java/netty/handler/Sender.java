package netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import netty._test.Header;
import netty._test.TransferData;

import java.util.List;

public class Sender extends MessageToMessageEncoder<TransferData> {

    int sum = 0;

    @Override
    protected void encode(ChannelHandlerContext ctx, TransferData dt, List<Object> list) throws Exception {
        list.add(dt.getHeader().getByteBuf());
        list.add(dt.getData());

        sum += dt.getData().readableBytes() + dt.getHeader().getByteBuf().readableBytes();
        if(sum > 260_000_000)
            System.out.println(sum);
    }
}
