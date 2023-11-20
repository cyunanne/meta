package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedStream;
import io.netty.handler.stream.ChunkedWriteHandler;
import netty.common.FileSpec;
import netty.common.Message;

import java.io.*;
import java.net.SocketAddress;

public class FileLoadHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        super.connect(ctx, remoteAddress, localAddress, promise);
        System.out.println("File Channel Connected.");
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        if( !(msg instanceof String) ) {
            ctx.writeAndFlush(msg);
            return;
        }

        try {
            String filePath = (String) msg;
            RandomAccessFile file = new RandomAccessFile(filePath, "r");
            ChunkedFile chunkedFile = new ChunkedFile(file, 0, file.length(), 8192);
            ctx.writeAndFlush(chunkedFile);

            // Stream
//            FileInputStream fis = new FileInputStream(filePath);
//            ChunkedStream chunkedStream = new ChunkedStream(fis);
//            ctx.writeAndFlush(chunkedStream).addListener(ChannelFutureListener.CLOSE);

        } catch (FileNotFoundException e) {
            System.out.println("파일을 찾을 수 없습니다.");
            ctx.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        System.out.println("File Channel Closed.");
    }
}