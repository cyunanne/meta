package netty.initializer;

import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.compression.ZstdEncoder;
import io.netty.handler.codec.string.StringDecoder;
import netty.handler.codec.FileEncoder;
import netty.handler.codec.MessageEncoder;
import netty.handler.Encryptor;
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

        pipeline.addLast(new FileEncoder());    // (3) add header + send
        pipeline.addLast(new Encryptor());      // (2) encrypt
        pipeline.addLast(new ZstdEncoder(3, 1024, 1024));    // () compress
        pipeline.addLast(new FileHandler());    // (1) load file
    }
}
