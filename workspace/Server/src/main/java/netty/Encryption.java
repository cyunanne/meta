package netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;

/**
 * ASE-256 암호화
 */
public class Encryption extends MessageToMessageEncoder<ByteBuf> {

    private final String key = "01234567890123456789012345678901"; // 32byte
    private final String iv = key.substring(0, 16); // 16byte

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

        // ByteBuf -> byte[] -> encrypt
        byte[] tmpArr = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(tmpArr);
        byte[] encrypted = cipher.doFinal(tmpArr);

        // byte[] -> ByteBuf
        ByteBuf buf = Unpooled.buffer().writeBytes(encrypted);
        list.add(buf);
    }
}
