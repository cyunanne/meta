package netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import javax.crypto.Cipher;
import java.io.*;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Client Connected : " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("channelRead");

        String message = msg instanceof byte[] ? new String((byte[])msg) : (String)msg;
        String port = ctx.channel().remoteAddress().toString().split(":")[1];
        if ("quit".equals(message)) {
            ctx.close();
            return;
        }
        System.out.println("Client" + port + " : " + message);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
//        System.out.println("channelReadComplete");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 파일 복호화(확인용)
//            decryption("testfile_enc", "testfile");
//            System.out.println("파일 복호화 완료");
    }

    private static void decryption(String src, String des) {
        try {
            InputStream input = new BufferedInputStream(new FileInputStream(src));
            OutputStream output = new BufferedOutputStream(new FileOutputStream(des));

            Cipher cipher = (new MyCipher('D')).getCipher();
            byte[] buffer = new byte[1024];
            int read = -1;
            while ((read = input.read(buffer)) != -1) {
                output.write(cipher.update(buffer, 0, read));
            }
            output.write(cipher.doFinal());

            input.close();
            output.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}