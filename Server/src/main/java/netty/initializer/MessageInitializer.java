package netty.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import netty.handler.MessageHandler;
import netty.handler.MessageParser;

public class MessageInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // Inbound
        pipeline.addLast(new MessageParser());  // (0) parse
        pipeline.addLast(new StringDecoder());  // (1) (문자열인 경우)
        pipeline.addLast(new MessageHandler()); // (2) print

        // Outbound
        pipeline.addLast(new StringEncoder());
    }
}