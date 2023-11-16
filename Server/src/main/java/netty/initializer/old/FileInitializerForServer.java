package netty.initializer.old;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import netty.handler.old.FileHandlerForServer;

public class FileInitializerForServer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // Outbound : File
        pipeline.addLast("FileHandler", new FileHandlerForServer());
    }

}
