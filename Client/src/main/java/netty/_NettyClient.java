package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.initializer.ClientInitializer;

import java.util.Scanner;

public class _NettyClient {

    protected String host;
    protected int port;
    protected EventLoopGroup eventLoopGroup;
    protected Bootstrap bootstrap;
    protected Channel channel;

    Scanner scanner = new Scanner(System.in);

    public _NettyClient(String host, int port, ChannelInitializer<SocketChannel> ci){
        this.host = host;
        this.port = port;

        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap().group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(ci);
    }

    public _NettyClient(String host, int port) {
        this(host, port, new ClientInitializer());
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
                String[] commands = msg.split(" ");

                if (commands[0].equals("quit")) break;
                if (commands.length != 2) {
                    System.out.print("명령어를 확인해주세요.\n>>> ");
                } else channel.writeAndFlush(msg);

//                else if (commands[0].equals("put")) {
//                    sendMessage(msg);
//                    sendFile(commands[1]);
//                }
            }

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
