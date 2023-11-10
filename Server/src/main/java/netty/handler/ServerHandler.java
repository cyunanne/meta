package netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.cipher.ASE256Cipher;

import javax.crypto.Cipher;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private OutputStream os;

    // test
    ASE256Cipher cipher = new ASE256Cipher(Cipher.DECRYPT_MODE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Client Connected : " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("channelRead~~~");

        if (msg instanceof String) {
            String message = (String) msg;
            if (message.equals("fin")) {
                closeFile();
            } else if (message.startsWith("get")) {

            }
            echoMessage(ctx, msg);

        } else {
            // origin
//            os.write((byte[]) msg);

            // test
            byte[] enc = (byte[]) msg;
            byte[] arr = cipher.update(enc, 0, enc.length);
            os.write(arr);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
//        System.out.println("channelReadComplete");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        closeFile();
        System.out.println("channel closed");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        ctx.close();
        cause.printStackTrace();
    }

    private void echoMessage(ChannelHandlerContext ctx, Object msg) throws IOException {
        String message = (String)msg;
        String port = ctx.channel().remoteAddress().toString().split(":")[1];
        System.out.println("Client" + port + " : " + message);

        if (message.startsWith("put")) {
            String filename = message.split(" ")[1];
            os = Files.newOutputStream(Paths.get(filename));
        } else {
            ctx.writeAndFlush(message);
        }
    }

    private void closeFile() {
        System.out.println("file closed");
        try {
            if (os != null) {
                os.flush();
                os.close();
//                os = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}