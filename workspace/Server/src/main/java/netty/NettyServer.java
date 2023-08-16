package netty;

import io.netty.bootstrap.ServerBootstrap;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.base64.Base64Decoder;
import io.netty.handler.codec.base64.Base64Encoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.crypto.Cipher;
import javax.net.ssl.SSLException;
import java.io.*;
import java.net.InetSocketAddress;
import java.security.spec.ECField;

public class NettyServer {

    private int port = 8888;
    private final static String certPath = "./src/main/resources/netty.crt"; // 인증서
    private final static String keyPath = "./src/main/resources/server.pem"; // 개인키

    public NettyServer(int port) {
        this.port = port;
    }

    public void run() {

        EventLoopGroup bossEventLoopGroup = new NioEventLoopGroup(); // Listen ServerSocket
        EventLoopGroup workerEventLoopGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossEventLoopGroup, workerEventLoopGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    FileHandler fileHandler = new FileHandler();

                    // SSL
                    File cert = new File(certPath); // 인증서
                    File key = new File(keyPath);   // 개인키
                    SslContext sslContext = SslContextBuilder.forServer(cert, key).build();
                    pipeline.addLast(sslContext.newHandler(ch.alloc()));

                    // outbound
//                    pipeline.addLast(new ServerOutboundHandler());
                    pipeline.addLast(new StringEncoder());
//                    pipeline.addLast(new StringDecoder());
                    pipeline.addLast(new ByteArrayEncoder());
                    pipeline.addLast(new ByteArrayDecoder());
//                    pipeline.addLast(new Encryption());
//                    pipeline.addLast(new Decryption());
//                    pipeline.addLast(new Base64Encoder());

                    // inbound
//                    pipeline.addLast(new ObjectEncoder());
                    pipeline.addLast(new ServerHandler());
                    pipeline.addLast(fileHandler);
                }
            });

            // Channel 생성후 기다림
            ChannelFuture bindFuture = bootstrap.bind(new InetSocketAddress(port)).sync();
            Channel channel = bindFuture.channel();

            // Channel이 닫힐 때까지 대기
            channel.closeFuture().sync();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);

        } finally {
            workerEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
            bossEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
        }
    }
}