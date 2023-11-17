package netty.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import netty.handler.FileLoadHandler;
import netty.handler.MessageHandler;

public class MessageInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // Inbound
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new MessageHandler());

        // Outbound
        pipeline.addLast(new StringEncoder());
    }
}
