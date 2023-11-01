package netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private OutputStream outputStream;
    private String filename;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Client Connected : " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("channelRead~~~");

        if (msg instanceof String) {
            echoMessage(ctx, msg);

        } else if (msg instanceof byte[]) {
            if(outputStream == null) {
                outputStream = new FileOutputStream(filename);
            }
            outputStream.write((byte[]) msg);
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
            ctx.writeAndFlush(commands[0]);
        } else if(commands[0].equals("put")) {
            filename = commands[1];
        }
    }

    private void closeFile() {
        try {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
                outputStream = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}