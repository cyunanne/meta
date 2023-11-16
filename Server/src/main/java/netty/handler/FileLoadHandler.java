package netty.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.stream.ChunkedStream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileLoadHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        String filePath = (String) msg;

        try {
            FileInputStream fis = new FileInputStream(filePath);
            ChunkedStream chunkedStream = new ChunkedStream(fis);
            ctx.writeAndFlush(chunkedStream).addListener(ChannelFutureListener.CLOSE);

        } catch (FileNotFoundException e) {
            System.out.println("파일을 찾을 수 없습니다.");
        }
    }
}