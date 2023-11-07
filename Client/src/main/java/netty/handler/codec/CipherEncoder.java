package netty.handler.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import netty.cipher.ASE256Cipher;

import java.io.IOException;

public class CipherEncoder extends ChannelOutboundHandlerAdapter {

    ASE256Cipher cipher = new ASE256Cipher('E');

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws IOException {
        if(msg instanceof String) {
            if(msg.equals("fin")) {
                ctx.writeAndFlush(cipher.doFinal());
            }
            ctx.writeAndFlush(msg);
        }

        byte[] plain = (byte[])msg;
        byte[] enc = cipher.update(plain, 0, plain.length);
        ctx.writeAndFlush(enc);
    }
}
