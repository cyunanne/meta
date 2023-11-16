package netty.handler.old;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import netty.cipher.ASE256Cipher;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;

import javax.crypto.Cipher;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

@ChannelHandler.Sharable
public class FileHandlerForServer extends ChannelInboundHandlerAdapter {

    private OutputStream os;

    private FileOutputStream fos;
    private FileSpec fileSpec;
    private Long received = 0L;

    // test
    private ASE256Cipher cipher = new ASE256Cipher(Cipher.DECRYPT_MODE);

    private io.netty.channel.Channel messageChannel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof io.netty.channel.Channel) {
            messageChannel = (io.netty.channel.Channel) msg;

        } else if (msg instanceof FileSpec) {
            fileSpec = (FileSpec) msg;
            fos = new FileOutputStream(fileSpec.getName());

        } else if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            fos.getChannel().write(byteBuf.nioBuffer());
            byteBuf.release();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
//        System.out.println("channelReadComplete");
        messageChannel.writeAndFlush(
                new TransferData(Header.TYPE_MSG, Header.CMD_PUT, true)
        );
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (fos != null) {
            fos.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        ctx.close();
        cause.printStackTrace();
    }
}