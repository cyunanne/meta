package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.initializer.MessageInitializer;
import netty.test.Header;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class NettyClient {

    protected String host;
    protected int port;
    protected EventLoopGroup eventLoopGroup;
    protected Bootstrap bootstrap;
    protected Channel channel;

    Scanner scanner = new Scanner(System.in);

    public NettyClient(String host, int port, ChannelInitializer<SocketChannel> ci){
        this.host = host;
        this.port = port;

        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap().group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(ci);
    }

    public NettyClient(String host, int port) {
        this(host, port, new MessageInitializer());
    }

    public Channel getChannel() {
        return channel;
    }

    public void run() {
        try {
            channel = bootstrap.connect(host, port).sync().channel();

            System.out.print(">>> ");
            while( true ) {
                String msg = scanner.nextLine();
                if (msg.equals("quit")) break;
                else {
                    this.sendFile("testfile");
                }
            }

            channel.closeFuture().sync();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    public void sendMessage(String msg) throws InterruptedException {
        channel.writeAndFlush(msg).sync();
    }

    public void sendFile(String filename) throws Exception {
        InputStream inputStream = new FileInputStream(filename);
        byte[] buffer = new byte[1024];
        int read = -1;

        while ((read = inputStream.read(buffer)) != -1) {
            channel.writeAndFlush(buffer);
        }

        System.out.println("파일 전송 완료");
        channel.writeAndFlush("__fin__");
    }

    public void stop() {
        channel.close();
        eventLoopGroup.shutdownGracefully();
    }
}
