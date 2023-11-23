package netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import netty.common.TransferData;

import java.util.List;

public class Sender extends MessageToMessageEncoder<TransferData> {

    @Override
    protected void encode(ChannelHandlerContext ctx, TransferData dt, List<Object> list) {
        list.add(dt.getHeader().toByteBuf());
        list.add(dt.getData());
    }
}
