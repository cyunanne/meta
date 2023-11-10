package netty.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringEncoder;
import netty.handler.FileHandler;
import netty.handler.codec.FileEncoder;
import netty.handler.codec.MessageEncoder;
import netty.handler.codec.Parsor;
import netty.handler.ServerHandler;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

//    private final static String certPath = "./src/main/resources/netty.crt"; // 인증서
//    private final static String keyPath = "./src/main/resources/server.pem"; // 개인키

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // SSL
//        File cert = new File(certPath); // 인증서
//        File key = new File(keyPath);   // 개인키
//        SslContext sslContext = SslContextBuilder.forServer(cert, key).build();
//        pipeline.addLast(sslContext.newHandler(ch.alloc()));

        // Outbound : Message
//        pipeline.addLast(new StringEncoder());
        pipeline.addLast(new MessageEncoder());

        // Outbound : File
        pipeline.addLast(new FileEncoder());        // (3) add header + send
        pipeline.addLast(new ByteArrayEncoder());   // (2) byte[] -> ByteBuf
        pipeline.addLast(new FileHandler());        // (1) load file

        // Inbound
        pipeline.addLast(new Parsor());
        pipeline.addLast(new ServerHandler());
    }
}