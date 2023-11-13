package netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.cipher.AES256Cipher;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class CipherDecoder extends ChannelInboundHandlerAdapter {

    private final int BLOCK_SIZE = 1024;
    private AES256Cipher cipher;

    int size = 0;

//    int size = 0;

/*    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("CipherDecoder.channelRead()");
        if(msg instanceof String) {
            if(((String)msg).startsWith("get")) {
                cipher = new ASE256Cipher(Cipher.DECRYPT_MODE);
            }
//            else if(msg.equals("fin-d")) {
//                ctx.fireChannelRead(cipher.doFinal((byte[]) msg));
//            }
            ctx.fireChannelRead(msg);

        } else {

//            size += ((byte[]) msg).length;
//            System.out.println(size);


            byte[] enc = (byte[]) msg;
            ctx.fireChannelRead(decrypt(enc));


//            byte[] plain = cipher.update(enc);
//            if(enc.length < BLOCK_SIZE) {
//                plain = cipher.doFinal(enc);
//            }
//            ctx.fireChannelRead(plain);
        }
    }*/

/*    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("CipherDecoder.channelRead()");
        if(msg instanceof String) {
            if(((String)msg).startsWith("get")) {
                cipher = new ASE256Cipher(Cipher.DECRYPT_MODE);
                size= 0;
            }
//            ctx.fireChannelRead(msg);
            ctx.fireChannelRead(msg);

        } else {

            ctx.fireChannelRead(msg);

//            byte[] enc = (byte[]) msg;
////            byte[] plain = cipher.update(enc);
////            ctx.fireChannelRead(cipher.update(enc));
////            ctx.fireChannelRead(plain);
//            size += enc.length;
//            System.out.println(size);
//
//            if(enc.length < BLOCK_SIZE) {
//                ctx.fireChannelRead(cipher.doFinal(enc));
//            } else {
//                ctx.fireChannelRead(cipher.update(enc));
//                before = enc;
//            }
        }
    }*/

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if(msg instanceof byte[]) {
            // decrypt

        } else if(msg instanceof String) {
            String message = (String) msg;
            if(message.startsWith("get")) {
                cipher = new AES256Cipher(Cipher.DECRYPT_MODE);
            }

        }
        ctx.fireChannelRead(msg);
    }

    private byte[] decrypt(byte[] encryptedBytes) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, IOException {

        ByteArrayInputStream inputStream = new ByteArrayInputStream(encryptedBytes);
        CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher.getCipher());
        byte[] buffer = new byte[encryptedBytes.length];
        int bytesRead;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
//        cipherInputStream.read(buffer);
//        outputStream.write(buffer);

        cipherInputStream.close();
        return outputStream.toByteArray();
    }
}
