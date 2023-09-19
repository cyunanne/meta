package netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import java.io.File;

public class MainChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final static String certPath = "./src/main/resources/netty.crt"; // 인증서
    private final static String keyPath = "./src/main/resources/server.pem"; // 개인키

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // SSL
        File cert = new File(certPath); // 인증서
        File key = new File(keyPath);   // 개인키
        SslContext sslContext = SslContextBuilder.forServer(cert, key).build();
        pipeline.addLast(sslContext.newHandler(ch.alloc()));

        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());
        pipeline.addLast(new ServerHandler());
    }
}