package netty.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import netty.handler.FileHandler;

public class _FileInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // SSL
//        SslContext sslContext = SslContextBuilder.forClient().build();
//        pipeline.addLast(sslContext.newHandler(ch.alloc()));

        pipeline.addLast(new ByteArrayEncoder());
        pipeline.addLast(new FileHandler());
    }
}
