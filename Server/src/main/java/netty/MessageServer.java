package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import netty.initializer.MessageInitializer;
import netty.initializer.old.ServerInitializer;

import java.net.InetSocketAddress;

public class MessageServer implements Runnable{

    private int port;
    private EventLoopGroup bossEventLoopGroup; // Listen ServerSocket
    private EventLoopGroup workerEventLoopGroup;
    private ServerBootstrap bootstrap;

    private Channel channel;

    public MessageServer(int port) {
        this.port = port;

        bossEventLoopGroup = new NioEventLoopGroup(); // Listen ServerSocket
        workerEventLoopGroup = new NioEventLoopGroup();

        // 채널 설정
        bootstrap = new ServerBootstrap();
        bootstrap.group(bossEventLoopGroup, workerEventLoopGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new MessageInitializer());
    }

    @Override
    public void run() {
        try {
            channel = bootstrap.bind(new InetSocketAddress(port)).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        workerEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
        bossEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
    }

}