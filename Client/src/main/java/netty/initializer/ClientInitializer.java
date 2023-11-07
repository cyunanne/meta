package netty.initializer;

import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.compression.ZstdEncoder;
import io.netty.handler.codec.string.StringDecoder;
import netty.handler.codec.*;
import netty.handler.FileHandler;
import netty.handler.MessageHandler;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // SSL
//        SslContext sslContext = SslContextBuilder.forClient().build();
//        pipeline.addLast(sslContext.newHandler(ch.alloc()));

        // Inbound
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new MessageHandler());

        // Outbound : Message
        pipeline.addLast(new MessageEncoder());

        // Outbound : File
        pipeline.addLast(new FileEncoderByteBuf());    // (4) add header + send
        pipeline.addLast(new CipherEncoderByteBuf());  // (3) encrypt
        pipeline.addLast(new ZstdEncoder());    // (2) compress
        pipeline.addLast(new ByteArrayEncoder());
        pipeline.addLast(new FileHandler());    // (1) load file
    }
}
