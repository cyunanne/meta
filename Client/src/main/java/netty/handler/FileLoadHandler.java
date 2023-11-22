package netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.stream.ChunkedFile;
import netty.common.FileSpec;
import netty.common.Header;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.net.SocketAddress;

public class FileLoadHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        super.connect(ctx, remoteAddress, localAddress, promise);
        System.out.println("Channel Connected.");
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        
        // 메타 데이터
        if(msg instanceof FileSpec) {
            ctx.writeAndFlush(msg);
        }

        // 파일 데이터
        else if(msg instanceof String) {
            String filePath = (String) msg;

            try {
                RandomAccessFile file = new RandomAccessFile(filePath, "r");
                ChunkedFile chunkedFile = new ChunkedFile(file, 0, file.length(), Header.CHUNK_SIZE);
                ctx.writeAndFlush(chunkedFile);

                // Stream
//            FileInputStream fis = new FileInputStream(filePath);
//            ChunkedStream chunkedStream = new ChunkedStream(fis);
//            ctx.writeAndFlush(chunkedStream)

            } catch (FileNotFoundException e) {
                System.out.println("파일을 찾을 수 없습니다.");
                ctx.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        System.out.println("File Channel Closed.");
    }

}