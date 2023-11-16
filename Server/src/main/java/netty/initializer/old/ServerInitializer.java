package netty.initializer.old;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import netty.handler.old.Sender;
import netty.handler.old.ServerHandler;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // Outbound
        pipeline.addLast(new Sender());

        // Inbound
//        pipeline.addLast(new Parsor());             // (1)
        pipeline.addLast(new ServerHandler());      // (2)
    }
}