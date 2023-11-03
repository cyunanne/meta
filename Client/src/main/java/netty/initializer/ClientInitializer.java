package netty.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import netty.codec.FileEncoder;
import netty.codec.MessageEncoder;
import netty.handler.FileHandler;
import netty.handler.MessageHandler;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // SSL
//        SslContext sslContext = SslContextBuilder.forClient().build();
//        pipeline.addLast(sslContext.newHandler(ch.alloc()));

        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new MessageHandler());

        pipeline.addLast(new MessageEncoder());
        pipeline.addLast(new FileEncoder());
        pipeline.addLast(new FileHandler());
    }
}
