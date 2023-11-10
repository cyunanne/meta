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

    ASE256Cipher cipher = new ASE256Cipher(Cipher.ENCRYPT_MODE);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws IOException {
//        System.out.println("CipherEncoderByeBuf.write()");
        if(msg instanceof String) {
            if(msg.equals("fin")) {
                ctx.writeAndFlush(Unpooled.wrappedBuffer(cipher.doFinal()));
            }
            ctx.writeAndFlush(msg);
        }

//        ByteBuf buf = (ByteBuf) msg;
//        int len = buf.readableBytes();
//        byte[] plain = new byte[len];
//        buf.readBytes(plain);
//        buf.release();

        byte[] data = ByteBufUtil.getBytes((ByteBuf) msg);

//        ctx.writeAndFlush(Unpooled.wrappedBuffer(cipher.update(plain)));
        ctx.writeAndFlush(Unpooled.wrappedBuffer(cipher.update(data)));
    }
}
