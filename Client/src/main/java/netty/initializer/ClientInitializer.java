package netty.initializer;

import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import netty.handler.FileHandlerForClient;
import netty.handler.codec.Sender;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // Outbound : File
        pipeline.addLast(new Sender());            // (5) add header + send
        pipeline.addLast(new FileHandlerForClient());   // (1) load file
    }
}
