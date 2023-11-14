package netty.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import netty.handler.HandlerForServer;
import netty.handler.Sender;
import netty.handler.codec.FileEncoder;
import netty.handler.codec.MessageEncoder;
import netty.handler.codec.Parsor;
import netty.handler.ServerHandler;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // Outbound
        pipeline.addLast(new Sender());

        // Inbound
        pipeline.addLast(new Parsor());             // (1)
        pipeline.addLast(new ServerHandler());      // (2)
    }
}