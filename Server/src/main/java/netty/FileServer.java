package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import netty.initializer.FileInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

public class FileServer {

    private final int port;
    private final EventLoopGroup bossEventLoopGroup; // Listen ServerSocket
    private final EventLoopGroup workerEventLoopGroup;
    private final ServerBootstrap bootstrap;

    private static final Logger logger = LogManager.getLogger(FileServer.class);

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

    public void run() {
        try {
            // 채널 연결 대기
            Channel channel = bootstrap.bind(new InetSocketAddress(port)).sync().channel();
            logger.info("Server Started. Listening port: " + port);

            channel.closeFuture().sync();

        } catch(InterruptedException e) {
            throw new RuntimeException(e);

        } finally {
            workerEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
            bossEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
        }
    }

}