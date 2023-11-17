package netty.handler.old;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import netty.common.TransferData;

import java.util.List;

public class Sender extends MessageToMessageEncoder<TransferData> {

    @Override
    protected void encode(ChannelHandlerContext ctx, TransferData dt, List<Object> list) throws Exception {
        list.add(dt.getHeader().getByteBuf());
        list.add(dt.getData());
    }
}
