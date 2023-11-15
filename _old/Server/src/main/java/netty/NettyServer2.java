package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class NettyServer2 {

    private int port;
    private EventLoopGroup bossEventLoopGroup; // Listen ServerSocket
    private EventLoopGroup workerEventLoopGroup;
    private ServerBootstrap bootstrap;

    public NettyServer2(int port, ChannelInitializer<SocketChannel> ci) {
        this.port = port;

        bossEventLoopGroup = new NioEventLoopGroup(); // Listen ServerSocket
        workerEventLoopGroup = new NioEventLoopGroup();

        bootstrap = new ServerBootstrap();
        bootstrap.group(bossEventLoopGroup, workerEventLoopGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(ci);
    }

    public NettyServer2(int port) {
        this(port, new MainServerInitializer());
    }

    public void run() {

        try {
            // 바인딩 & 리스닝
            ChannelFuture bindFuture = bootstrap.bind(new InetSocketAddress(port)).sync();
            Channel channel = bindFuture.channel();

            // 채널 닫힐 때까지 프로그램 대기
            channel.closeFuture().sync();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            workerEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
            bossEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
        }
    }

}