package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.cipher.ASE256Cipher;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private OutputStream os;
    private String filename;

    // test
    ASE256Cipher cipher = new ASE256Cipher('D');

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Client Connected : " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("channelRead~~~");

        if (msg instanceof String) {
            if(msg.equals("fin")) {
               os.write(cipher.doFinal());
               os.flush();
               os.close();
            }
            echoMessage(ctx, msg);

        } else {
            if(os == null) {
                os = Files.newOutputStream(Paths.get(filename));
            }
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

        String[] commands = ((String) msg).split(" ");
        if(commands[0].equals("fin")) {
            closeFile();
        }

        if(commands.length != 2) {
            ctx.writeAndFlush(msg);
        } else if(commands[0].equals("put")) {
            filename = commands[1];
        }
    }

    private void closeFile() {
        System.out.println("file closed");
        try {
            if (os != null) {
                os.flush();
                os.close();
                os = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}