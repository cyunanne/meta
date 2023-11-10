package netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import netty.cipher.ASE256Cipher;

import javax.crypto.Cipher;
import java.io.IOException;

public class CipherEncoder extends ChannelOutboundHandlerAdapter {

    private ASE256Cipher cipher;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws IOException {
//        System.out.println("CipherEncoderByeBuf.write()");
        if(msg instanceof String) {
            if(((String)msg).startsWith("put")) {
                cipher = new ASE256Cipher(Cipher.ENCRYPT_MODE);
            } else if(msg.equals("fin")) {
                ctx.writeAndFlush(Unpooled.wrappedBuffer(cipher.doFinal()));
            }
            ctx.writeAndFlush(msg);

        } else {
            byte[] data = ByteBufUtil.getBytes((ByteBuf) msg);
            ctx.writeAndFlush(Unpooled.wrappedBuffer(cipher.update(data)));
        }
    }
}
