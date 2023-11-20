package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class EncoderTest2 extends MessageToMessageEncoder<ByteBuf> {

    private static int BLOCK_SIZE = 8192;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // TODO 압축

        if(byteBuf.readableBytes() < BLOCK_SIZE) {
            System.out.println("압축 끝!");
        }

        list.add(byteBuf.retain());
    }
}
