package netty.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.stream.ChunkedWriteHandler;
import netty.handler.ServerHandler;
import netty.handler.codec.Parsor;
import netty.handler.codec._FileEncoder;
import netty.handler.codec._Parsor;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // Outbound
//        pipeline.addLast(new Sender());

        // Inbound
        pipeline.addLast(new _Parsor());             // (1)
        pipeline.addLast(new ServerHandler());      // (2)
//        pipeline.addLast(new ChunkedWriteHandler());
//        pipeline.addLast(new _FileEncoder());
    }
}