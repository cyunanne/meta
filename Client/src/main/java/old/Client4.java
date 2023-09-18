package old;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import java.util.Scanner;

public class Client4 {

    private static final String host = "localhost";
    private static final int port = 8888;

    private static Scanner scanner;

    public static void main(String[] args) throws Exception {
//        SocketWithNetty sock = new SocketWithNetty(host, port);

        scanner = new Scanner(System.in);
        String filename = scanner.nextLine();
        if(!"quit".equals(filename)){
//            sock.run("put", filename);
        }
    }
}