package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import netty.initializer.FileInitializer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class FileServer implements Runnable {

    private int port;
    private EventLoopGroup bossEventLoopGroup; // Listen ServerSocket
    private EventLoopGroup workerEventLoopGroup;
    private ServerBootstrap bootstrap;

    public FileServer(int port) {
        this.port = port;

        bossEventLoopGroup = new NioEventLoopGroup(); // Listen ServerSocket
        workerEventLoopGroup = new NioEventLoopGroup();

        // 채널 설정
        bootstrap = new ServerBootstrap();
        bootstrap.group(bossEventLoopGroup, workerEventLoopGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new FileInitializer());
    }

    @Override
    public void run() {
        try {
            // 채널 연결 대기
            Channel channel = bootstrap.bind(new InetSocketAddress(port)).sync().channel();
            System.out.println("Server Started.");

            channel.closeFuture().sync();

        } catch(InterruptedException e) {
            throw new RuntimeException(e);

        } finally {
            workerEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
            bossEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
        }
    }

}