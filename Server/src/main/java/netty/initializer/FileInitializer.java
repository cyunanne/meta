package netty.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import netty.handler.FileHandler;
import netty.handler.FileSaveHandler;
import netty.handler.MessageHandler;
import netty.handler.MessageOutboundHandler;

public class FileInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // Download (outbound)
//        pipeline.addLast(new ChunkedWriteHandler());    // (2) chunk & send
        pipeline.addLast(new MessageOutboundHandler());

        // Upload (inbound)
        pipeline.addLast(new FileSaveHandler());    // file save

    }
}
