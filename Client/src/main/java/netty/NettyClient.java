package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.initializer.ClientInitializer;

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
        this(host, port, new ClientInitializer());
    }

    public Channel getChannel() {
        return channel;
    }

    public void run() {
        try {

            System.out.print(">>> ");
            while( true ) {
                String filename = scanner.nextLine();
                if(filename.equals("quit")) break;

                channel = bootstrap.connect(host, port).sync().channel();
                channel.writeAndFlush(filename);
            }
            channel.close();

            System.out.println("프로그램을 종료합니다.");
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

//    public void sendMessage(String msg) throws InterruptedException {
//        channel.writeAndFlush(msg);
//    }
//    public void sendFile(String filename) throws Exception {
//
//        try {
//            InputStream inputStream = Files.newInputStream(Paths.get(filename));
//            byte[] buffer = new byte[1024];
//            while (inputStream.read(buffer) != -1) {
//                channel.writeAndFlush(buffer);
//            }
//            sendMessage("fin");
//        } catch (NoSuchFileException e) {
//            System.out.println("존재하지 않는 파일입니다.");
//        }
//    }

    public void downloadFile(String filename) throws Exception {

    }


    public void stop() {
        channel.close();
        eventLoopGroup.shutdownGracefully();
    }
}
