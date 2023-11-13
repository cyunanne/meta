package netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import netty.cipher.AES256Cipher;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import java.io.ByteArrayOutputStream;

public class CipherEncoder extends ChannelOutboundHandlerAdapter {

    private AES256Cipher cipher;
    ByteArrayOutputStream outputStream;
    CipherOutputStream cipherOutputStream;

/*    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//        System.out.println("CipherEncoderByeBuf.write()");
        if(msg instanceof String) {
            if(((String)msg).startsWith("put")) {
                cipher = new ASE256Cipher(Cipher.ENCRYPT_MODE);
                outputStream = new ByteArrayOutputStream();
                cipherOutputStream = new CipherOutputStream(outputStream, cipher.getCipher());
            } else if(((String)msg).startsWith("fin")) {
                cipherOutputStream.close();
            }
            ctx.writeAndFlush(msg);

        } else {
            byte[] data = ByteBufUtil.getBytes((ByteBuf) msg);
            cipherOutputStream.write(data);
            ctx.writeAndFlush(Unpooled.wrappedBuffer(outputStream.toByteArray()));


//            ByteBuf buf = Unpooled.wrappedBuffer(cipher.update(data));
//            if(data.length < BLOCK_SIZE) {
//                buf = Unpooled.wrappedBuffer(cipher.doFinal(data));
//            }
//            ctx.writeAndFlush(buf);
        }
    }*/

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//        System.out.println("CipherEncoderByeBuf.write()");
        if(msg instanceof String) {
            if(((String)msg).startsWith("put")) {
                cipher = new AES256Cipher(Cipher.ENCRYPT_MODE);
            }
            ctx.writeAndFlush(msg);

        } else {
            byte[] data = ByteBufUtil.getBytes((ByteBuf) msg);

            if(data.length < 1024) {
                ctx.writeAndFlush(Unpooled.wrappedBuffer(cipher.doFinal(data)));
            } else  {
                ctx.writeAndFlush(Unpooled.wrappedBuffer(cipher.update(data)));
            }
        }
    }
}
