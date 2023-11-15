package netty.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import netty.handler._HandlerForServer;
import netty.handler.ServerHandler;
import netty.handler.codec._FileEncoder;
import netty.handler.codec._MessageEncoder;
import netty.handler.codec.Parsor;

public class _ServerInitializer extends ChannelInitializer<SocketChannel> {

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
        pipeline.addLast(new _MessageEncoder());

        // Outbound : File
        pipeline.addLast(new _FileEncoder());        // (3) add header + send
        pipeline.addLast(new ByteArrayEncoder());   // (2) byte[] -> ByteBuf
        pipeline.addLast(new _HandlerForServer());        // (1) load file

        // Inbound
        pipeline.addLast(new Parsor());
        pipeline.addLast(new ServerHandler());
    }
}