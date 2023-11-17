package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDateTime;

public class FileSaveHandler extends ChannelInboundHandlerAdapter {

    private FileOutputStream fos;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws FileNotFoundException {
        System.out.println("Client Connected : " + ctx.channel().remoteAddress());
        String tmpFilePath = "upload." + LocalDateTime.now().toString().replaceAll("[^0-9]", "");
        fos = new FileOutputStream(tmpFilePath);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if( !(msg instanceof ByteBuf) ) return;

        ByteBuf byteBuf = (ByteBuf) msg;
        fos.getChannel().write(byteBuf.nioBuffer());
        byteBuf.release();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        fos.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        cause.printStackTrace();
    }

}