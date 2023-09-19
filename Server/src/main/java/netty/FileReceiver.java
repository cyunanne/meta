package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class FileReceiver {

    private int port = 8889;

    private EventLoopGroup bossEventLoopGroup; // Listen ServerSocket
    private EventLoopGroup workerEventLoopGroup;
    private ServerBootstrap bootstrap;
    private Channel channel;

//    private ChannelFuture channelFuture;

    public FileReceiver(int port) {
        this.port = port;
        connect();
    }

    private void connect() {
        try {
            bossEventLoopGroup = new NioEventLoopGroup(); // Listen ServerSocket
            workerEventLoopGroup = new NioEventLoopGroup();
            bootstrap = new ServerBootstrap();
            bootstrap.group(bossEventLoopGroup, workerEventLoopGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new PutFileChannelInitializer());
//            channel = bootstrap.bind(new InetSocketAddress(port)).sync().channel();
//            channelFuture = bootstrap.bind(new InetSocketAddress(port));

        } catch(Exception e) {
            System.out.println("리스너 설정 중 오류 발생");
            e.printStackTrace();
        }
    }

    public void run() throws Exception {
//        Channel channel =
        channel = bootstrap.bind(new InetSocketAddress(port)).sync().channel();


        try {
//            channelFuture.channel().closeFuture().sync(); // 채널 닫힐 때까지 프로그램 대기
            channel.closeFuture().sync();
            workerEventLoopGroup.shutdownGracefully();
            bossEventLoopGroup.shutdownGracefully();
        } catch(Exception e) {
            System.err.println("연결 종료 중 에러 발생");
            e.printStackTrace();
        }
    }
}