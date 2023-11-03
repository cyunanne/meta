package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import netty.initializer._FileInitializer;
import netty.initializer.ServerInitializer;

import java.net.InetSocketAddress;

public class NettyServer2 {

    private int port;
    private EventLoopGroup bossEventLoopGroup; // Listen ServerSocket
    private EventLoopGroup workerEventLoopGroup;
    private ServerBootstrap bootstrap;
    private ServerBootstrap bootstrap2;

    public NettyServer2(
            int port,
            ChannelInitializer<SocketChannel> msgInit,
            ChannelInitializer<SocketChannel> fileInit
    ) {
        this.port = port;

        bossEventLoopGroup = new NioEventLoopGroup(); // Listen ServerSocket
        workerEventLoopGroup = new NioEventLoopGroup();

        // 메시지 채널 설정
        bootstrap = new ServerBootstrap();
        bootstrap.group(bossEventLoopGroup, workerEventLoopGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(msgInit);

        // 파일 채널 설정
        bootstrap2 = new ServerBootstrap();
        bootstrap2.group(bossEventLoopGroup, workerEventLoopGroup);
        bootstrap2.channel(NioServerSocketChannel.class);
        bootstrap2.childHandler(fileInit);
    }

    public NettyServer2(int port) {
        this(
                port,
                new ServerInitializer(),
                new _FileInitializer()
        );
    }

    public void run() {

        try {
            // 채널 연결 대기
            ChannelFuture bindFuture = bootstrap.bind(new InetSocketAddress(port)).sync();
            Channel channel = bindFuture.channel();

            ChannelFuture bindFuture2 = bootstrap2.bind(new InetSocketAddress(port+1)).sync();
            Channel channel2 = bindFuture2.channel();

            // 채널 닫힐 때까지 프로그램 대기
            channel.closeFuture().sync();
            channel2.closeFuture().sync();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            stop();
        }
    }

    public void stop() {
        workerEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
        bossEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
    }

}