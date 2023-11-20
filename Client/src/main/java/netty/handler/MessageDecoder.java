package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import netty.cipher.AES256Cipher;
import netty.common.FileSpec;
import netty.common.Message;

import javax.crypto.Cipher;
import java.util.List;

public class MessageDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        if(byteBuf.readableBytes() == 316) {
            Message header = new Message(byteBuf.readByte());
            int len = header.setLength(byteBuf.readUnsignedShort());
            list.add(new FileSpec(byteBuf.readBytes(len).retain()));
            // 여기서오류남

        } else {
            list.add(byteBuf);
        }
    }
}

