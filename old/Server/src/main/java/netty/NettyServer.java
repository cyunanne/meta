package netty;

import io.netty.bootstrap.ServerBootstrap;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class NettyServer {

    private int port;
//    private int port2 = 8889;

    private EventLoopGroup bossEventLoopGroup; // Listen ServerSocket
    private EventLoopGroup workerEventLoopGroup;
    private ServerBootstrap bootstrap; // 메인 채널
//    private ServerBootstrap bootstrap2; // 파일 수신 채널
//    private ServerBootstrap bootstrap3; // 파일 송신 채널

//    public NettyServer(int port, int port2) {
//        this.port = port;
//        this.port2 = port2;
//
//        bossEventLoopGroup = new NioEventLoopGroup(); // Listen ServerSocket
//        workerEventLoopGroup = new NioEventLoopGroup();
//
//        // 메인 채널 설정
//        bootstrap = new ServerBootstrap();
//        bootstrap.group(bossEventLoopGroup, workerEventLoopGroup);
//        bootstrap.channel(NioServerSocketChannel.class);
//        bootstrap.childHandler(new MainChannelInitializer());
//
//        // 파일 수신 채널 설정
//        bootstrap2 = new ServerBootstrap();
//        bootstrap2.group(bossEventLoopGroup, workerEventLoopGroup);
//        bootstrap2.channel(NioServerSocketChannel.class);
//        bootstrap2.childHandler(new PutFileChannelInitializer());
//
//        // 파일 송신 채널 설정
//        bootstrap3 = new ServerBootstrap();
//        bootstrap3.group(bossEventLoopGroup, workerEventLoopGroup);
//        bootstrap3.channel(NioServerSocketChannel.class);
//        bootstrap3.childHandler(new GetFileChannelInitializer());
//
//    }

    public NettyServer(int port, ChannelInitializer<SocketChannel> ci) {
        this.port = port;

        bossEventLoopGroup = new NioEventLoopGroup(); // Listen ServerSocket
        workerEventLoopGroup = new NioEventLoopGroup();

        bootstrap = new ServerBootstrap();
        bootstrap.group(bossEventLoopGroup, workerEventLoopGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(ci);
    }

    public NettyServer(int port) {
        this(port, new MainServerInitializer());
    }

    public void run() {

        try {
            // 메인 채널 연결 대기
            ChannelFuture bindFuture = bootstrap.bind(new InetSocketAddress(port)).sync();
            Channel channel = bindFuture.channel();

            // 파일 전송 채널 연결 대기
//            bootstrap2.bind(new InetSocketAddress(port2)).sync();
//            bootstrap3.bind(new InetSocketAddress(port+2)).sync();

            // 메인 채널 닫힐 때까지 프로그램 대기
            channel.closeFuture().sync();

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