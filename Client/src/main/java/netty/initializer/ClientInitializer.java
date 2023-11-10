package netty.initializer;

import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.compression.ZstdEncoder;
import netty.handler.codec.*;
import netty.handler.FileHandler;
import netty.handler.ClientHandler;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // SSL
//        SslContext sslContext = SslContextBuilder.forClient().build();
//        pipeline.addLast(sslContext.newHandler(ch.alloc()));

        // Inbound
        pipeline.addLast(new Parsor());             // (1) header detach + ByteBuf -> byte[]
        pipeline.addLast(new CipherDecoder());      // (2) decrypt
        pipeline.addLast(new ClientHandler());      // (3) save file

        // Outbound : Message
        pipeline.addLast(new MessageEncoder());

        // Outbound : File
        pipeline.addLast(new FileEncoder());        // (5) add header + send
        pipeline.addLast(new CipherEncoder());      // (4) encrypt
//        pipeline.addLast(new ZstdEncoder());        // (3) compress
        pipeline.addLast(new ByteArrayEncoder());   // (2) byte[] -> ByteBuf
        pipeline.addLast(new FileHandler());        // (1) load file
    }
}
