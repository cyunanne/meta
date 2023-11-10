package netty.handler.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.cipher.ASE256Cipher;

import javax.crypto.Cipher;

public class CipherDecoder extends ChannelInboundHandlerAdapter {

    private ASE256Cipher cipher;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("CipherDecoder.channelRead()");
        if(msg instanceof String) {
            if(((String)msg).startsWith("get")) {
                cipher = new ASE256Cipher(Cipher.DECRYPT_MODE);
            } else if(msg.equals("fin-d")) {
                ctx.fireChannelRead(cipher.doFinal());
            }
            ctx.fireChannelRead(msg);

        } else {
            ctx.fireChannelRead(cipher.update((byte[]) msg));
        }
    }
}
