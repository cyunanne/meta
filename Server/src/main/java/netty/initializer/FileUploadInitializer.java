package netty.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.stream.ChunkedWriteHandler;
import netty.handler.FileLoadHandler;

public class FileUploadInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new ChunkedWriteHandler());    // (2) chunk & send
        pipeline.addLast(new FileLoadHandler());        // (1) file load
    }
}
