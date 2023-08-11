package netty;

import io.netty.bootstrap.ServerBootstrap;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;
import java.io.File;
import java.net.InetSocketAddress;

public class NettyServer {

    private int port = 8888;
    private final static String certPath = "./src/main/resources/netty.crt"; // 인증서
    private final static String keyPath = "./src/main/resources/server.pem"; // 개인키

    public NettyServer(int port) {
        this.port = port;
    }

    public void run() {

        EventLoopGroup bossEventLoopGroup = new NioEventLoopGroup(); // Listen ServerSocket
        EventLoopGroup workerEventLoopGroup = new NioEventLoopGroup(); // 만들어진 Channel에서 넘어온 이벤트 처리

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossEventLoopGroup, workerEventLoopGroup);

            bootstrap.channel(NioServerSocketChannel.class);
//            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    // SSL
//                    File cert = new File(certPath); // 인증서
//                    File key = new File(keyPath);   // 개인키
//                    SslContext sslContext = SslContextBuilder.forServer(cert, key).build();
//                    pipeline.addLast(sslContext.newHandler(ch.alloc()));

                    // 암호화
                    pipeline.addLast(new Encryption());
                    pipeline.addLast(new Decryption());

                    // default
                    pipeline.addLast(new ServerHandler());
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

    private static SslContext getSslContext() throws SSLException {
        File cert = new File(certPath); // 인증서 파일
        File key = new File(keyPath);   // 개인키 파일

        return SslContextBuilder.forServer(cert, key).build();
    }
}