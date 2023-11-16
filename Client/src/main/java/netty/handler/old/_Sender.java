package netty.handler.old;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import netty.common.TransferData;

import java.util.List;

public class _Sender extends MessageToMessageEncoder<TransferData> {

    int sum = 0;

    @Override
    protected void encode(ChannelHandlerContext ctx, TransferData dt, List<Object> list) throws Exception {



        list.add(dt.getHeader().getByteBuf());
        list.add(dt.getData());

        sum += dt.getHeader().getLength();
        System.out.println(sum);
    }
}
