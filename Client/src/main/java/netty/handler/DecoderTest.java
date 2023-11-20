package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import netty.cipher.AES256Cipher;

import javax.crypto.Cipher;
import java.util.List;

public class DecoderTest extends MessageToMessageDecoder<ByteBuf> {

    private static int BLOCK_SIZE = 8192;

    private AES256Cipher cipher = new AES256Cipher(Cipher.DECRYPT_MODE);

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // TODO 복호화
        int len = byteBuf.readableBytes();
        byte[] plain = new byte[len];
        byteBuf.readBytes(plain);

        System.out.println(len);

        byte[] enc = len < BLOCK_SIZE ? cipher.doFinal(plain) : cipher.update(plain);
        list.add(Unpooled.wrappedBuffer(enc));
    }
}

