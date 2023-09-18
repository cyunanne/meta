import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;

import java.util.Scanner;

public class SocketWithNetty {

    private String host = "localhost";
    private int port = 8888;

    private EventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap;

    public SocketWithNetty() {}

    public SocketWithNetty(String host, int port) throws Exception {
        this.host = host;
        this.port = port;

        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap().group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
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

    private void setHandler(String command) {
        switch (command) {
            case "put" -> bootstrap.handler(new PutFileChannelInitializer());
            case "get" -> bootstrap.handler(new GetFileChannelInitializer());
            default -> {}
        }
    }

    public void run() throws Exception {

        Scanner scanner = new Scanner(System.in);
        Channel channel = null;

        try {
            while(true) {
                System.out.print(">>> ");
                String command = scanner.nextLine();
                if (command.equals("quit")) break;

                setHandler(command);
                channel = bootstrap.connect(host, port).sync().channel();

                System.out.print("filename >>> ");
                String filename = scanner.nextLine();

                channel.writeAndFlush(filename);
                channel.close().sync();
            }
        } finally{
            if(channel != null && channel.isOpen()) {
                channel.close().sync();
            }
            eventLoopGroup.shutdownGracefully();
        }
    }

}
