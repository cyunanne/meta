package netty.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import netty.handler.ClientHandler;
import netty.handler.FileHandlerForClient;
import netty.handler.codec.*;

public class _ClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // SSL
//        SslContext sslContext = SslContextBuilder.forClient().build();
//        pipeline.addLast(sslContext.newHandler(ch.alloc()));

        // Outbound : Message
        pipeline.addLast(new MessageEncoder());

        // Outbound : File
//        pipeline.addLast(new FileEncoder());            // (5) add header + send
//        pipeline.addLast(new CipherEncoder());          // (4) encrypt
//        pipeline.addLast(new ZstdEncoder());          // (3) compress
        pipeline.addLast(new ByteArrayEncoder());       // (2) byte[] -> ByteBuf
        pipeline.addLast(new FileHandlerForClient());   // (1) load file

        // Inbound
        pipeline.addLast(new Parsor());             // (1) header detach + ByteBuf -> byte[] / String
        pipeline.addLast(new CipherDecoder());      // (2) decrypt
        pipeline.addLast(new ClientHandler());      // (3) save file
    }
}
