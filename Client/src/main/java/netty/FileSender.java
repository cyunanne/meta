package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.initializer.FileInitializer;

import java.util.Scanner;

public class FileSender extends NettyClient {

    private String filename;

    public FileSender(String host, int port, String filename) throws Exception {
        super(host, port, new FileInitializer());
        this.filename = filename;
    }

    @Override
    public void run() {
        try {
            channel = bootstrap.connect(host, port).sync().channel();
            System.out.println("파일채널 연결");
            System.out.println("파일 전송 시작");
            channel.writeAndFlush(filename).sync();
            System.out.println("다...리..좀....떨지말아주세요.....");

        } catch(Exception e) {
            e.printStackTrace();
            stop();
        } finally {
//            channel.close();
//            eventLoopGroup.shutdownGracefully();
        }
    }

    public void stop() {
        channel.close();
        eventLoopGroup.shutdownGracefully();
    }
}
