import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToMessageEncoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;

/**
 * ASE-256 암호화
 */
public class Encryption extends MessageToMessageEncoder<ByteBuf> {
//public class Encryption extends ChannelOutboundHandlerAdapter {
//
    private final String key = "01234567890123456789012345678901"; // 32byte
    private final String iv = key.substring(0, 16); // 16byte
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
    IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);
//
//    @Override
//    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
//        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
//        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);
//
//        //B yteBuf -> byte[] -> encrypt
////        ByteBuf buf = (ByteBuf)msg;
////        byte[] tmpArr = new byte[buf.readableBytes()];
////        buf.readBytes(tmpArr);
////        byte[] encrypted = cipher.doFinal(tmpArr);
//
//
//        String message = (String)msg;
//        byte[] encrypted = cipher.doFinal(message.getBytes());
//
//        // byte[] -> ByteBuf
////        buf = Unpooled.buffer().writeBytes(encrypted);
//
//
//        ctx.write(encrypted);
//    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {



        // ByteBuf -> byte[] -> encrypt
        byte[] tmpArr = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(tmpArr);
        byte[] encrypted = cipher.doFinal(tmpArr);

        // byte[] -> ByteBuf
        ByteBuf buf = Unpooled.buffer().writeBytes(encrypted);
        list.add(buf);
    }
}
