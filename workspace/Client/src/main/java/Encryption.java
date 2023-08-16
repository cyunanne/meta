//import io.netty.channel.*;
//
//import javax.crypto.Cipher;
//import javax.crypto.NoSuchPaddingException;
//import javax.crypto.spec.IvParameterSpec;
//import javax.crypto.spec.SecretKeySpec;
//import java.security.InvalidAlgorithmParameterException;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;
//
///**
// * ASE-256 암호화
// */
//public class Encryption extends ChannelOutboundHandlerAdapter {
//
//    private final String key = "01234567890123456789012345678901"; // 32byte
//    private final String iv = key.substring(0, 16); // 16byte
//
//    private Cipher cipher;
//    private SecretKeySpec keySpec;// = new SecretKeySpec(key.getBytes(), "AES");
//    private IvParameterSpec ivParamSpec;// = new IvParameterSpec(iv.getBytes());
//
//
//    public Encryption() throws Exception {
//        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        keySpec = new SecretKeySpec(key.getBytes(), "AES");
//        ivParamSpec = new IvParameterSpec(iv.getBytes());
//        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);
//    }
//
//    @Override
//    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//        System.out.println("Encryption");
//        String message = null;
//        if(msg instanceof String) message = (String)msg;
//        else if(msg instanceof byte[]) message = new String((byte[])msg);
//
//        byte[] encrypted = cipher.doFinal(message.getBytes());
//        ctx.writeAndFlush(encrypted);
//    }
//}

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;

/**
 * ASE-256 암호화
 */
//public class Encryption extends ChannelOutboundHandlerAdapter {
public class Encryption extends MessageToMessageEncoder<byte[]> {
    private final String key = "01234567890123456789012345678901"; // 32byte
    private final String iv = key.substring(0, 16); // 16byte

    private Cipher cipher;
    private SecretKeySpec keySpec;// = new SecretKeySpec(key.getBytes(), "AES");
    private IvParameterSpec ivParamSpec;// = new IvParameterSpec(iv.getBytes());


    public Encryption() throws Exception {
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        keySpec = new SecretKeySpec(key.getBytes(), "AES");
        ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] msg, List<Object> list) throws Exception {
        System.out.println("Encryption");
        byte[] encrypted = cipher.doFinal(msg);
        ctx.writeAndFlush(encrypted);
    }

}