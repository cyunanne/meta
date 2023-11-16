package netty.handler.old;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import netty.common.TransferData;

import java.util.List;

public class Sender extends MessageToMessageEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> list) throws Exception {
        System.out.println("지나가나?");
//        list.add(msg);
    }
}
