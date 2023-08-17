package netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import javax.crypto.Cipher;
import java.io.*;

public class FileHandler extends ChannelInboundHandlerAdapter {

    private OutputStream outputStream;

    public FileHandler() throws Exception {
        outputStream = new FileOutputStream("testfile_enc");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel active");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("read");

        // 암호화 파일 저장
        if (outputStream == null) {
            outputStream = new FileOutputStream("testfile_enc", true);
        }

        outputStream.write((byte[]) msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelReadComplete");

        if(ctx.channel().bytesBeforeWritable() == 0) {
            outputStream.close();


        }
    }



}
