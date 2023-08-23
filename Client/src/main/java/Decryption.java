//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.Unpooled;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInboundHandlerAdapter;
//import io.netty.handler.codec.MessageToMessageDecoder;
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.IvParameterSpec;
//import javax.crypto.spec.SecretKeySpec;
//import java.util.List;
//
///**
// * ASE-256 복호화
// */
//public class Decryption extends ChannelInboundHandlerAdapter {
//
//    private final String key = "01234567890123456789012345678901"; // 32byte
//    private final String iv = key.substring(0, 16); // 16byte
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("Decryption");
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
//        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
//        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);
//
//        ctx.fireChannelRead(cipher.doFinal((byte[]) msg));
//    }
//}

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;

/**
 * ASE-256 복호화
 */
//public class Decryption extends ChannelInboundHandlerAdapter {
public class Decryption extends MessageToMessageDecoder<byte[]> {

    private final String key = "01234567890123456789012345678901"; // 32byte
    private final String iv = key.substring(0, 16); // 16byte

    @Override
    protected void decode(ChannelHandlerContext ctx, byte[] msg, List<Object> list) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

        ctx.fireChannelRead(cipher.doFinal(msg));
    }

}
