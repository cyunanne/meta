package netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.stream.ChunkedFile;
import netty.common.FileSpec;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileLoadHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        if( !(msg instanceof String) ) {
            ctx.writeAndFlush(msg);
            return;
        }

        // 파일 정보 전송
        String filePath = (String) msg;
        FileSpec fs = new FileSpec(filePath).setEncrypted(true);
        ctx.writeAndFlush(fs);

        // 파일 전송
        try {
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
        } catch (IOException e) {
//            throw new RuntimeException(e);
            e.printStackTrace();
        }
    }

}