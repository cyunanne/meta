import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;

public class FileSender2 {

    private String host;
    private int port;

    private EventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap;

    private Channel channel;
//    private ChannelFuture channelFuture;

    public FileSender2() {}

    public FileSender2(String host, int port) throws Exception {
        this.host = host;
        this.port = port;

        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap().group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new PutFileChannelInitializer());
    }

    public void disconnect() {
        try {
            channel.close().sync();
            eventLoopGroup.shutdownGracefully();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void run() throws Exception {

        channel = bootstrap.connect(host, port).sync().channel(); // 서버 연결

        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("filename >>> ");
            String filename = scanner.nextLine();
            if (!filename.equals("quit")) {
                channel.writeAndFlush(filename);
            }

        } finally {
            channel.close();
            eventLoopGroup.shutdownGracefully();
        }
    }

}
