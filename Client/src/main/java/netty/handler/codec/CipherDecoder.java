package netty.handler.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.cipher.ASE256Cipher;

import javax.crypto.Cipher;

public class CipherDecoder extends ChannelInboundHandlerAdapter {

    ASE256Cipher cipher = new ASE256Cipher(Cipher.DECRYPT_MODE);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("CipherDecoder.channelRead()");
        if(msg instanceof String) {
            ctx.fireChannelRead(msg);

        } else {
            byte[] data = (byte[]) msg;
            ctx.fireChannelRead(cipher.update(data));
        }
    }
}
