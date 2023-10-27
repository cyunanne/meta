import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;

public class NettyClient {

    protected String host;
    protected int port;
    protected EventLoopGroup eventLoopGroup;
    protected Bootstrap bootstrap;
    protected Channel channel;

    public NettyClient(String host, int port, ChannelInitializer<SocketChannel> ci){
        this.host = host;
        this.port = port;

        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap().group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(ci);
    }

    public NettyClient(String host, int port) {
        this(host, port, new MainClientInitializer());
    }

    public void run() {
        try {
            channel = bootstrap.connect(host, port).sync().channel();

            System.out.print(">>> ");
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();
            channel.writeAndFlush(command).sync();

        } catch(Exception e) {
            stop();
            e.printStackTrace();
        }
    }

    public void stop() {
        channel.close();
        eventLoopGroup.shutdownGracefully();
    }
}
