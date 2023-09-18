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

/**
 * 안됨 ^^!
 */
public class Client2 {

    private static String host = "localhost";
    private static int port = 8888;

    public static void main(String[] args) throws Exception {
        while( getCommand() );
    }

    public static boolean getCommand() throws Exception {

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if("quit".equals(input)) return false;

        String[] inputs = input.split(" ");
        if(inputs.length < 2) {
            System.out.println("잘못 입력하셨습니다.");
            return true;
        }

        String command = inputs[0];
        String filename = inputs[1];

//        new SocketWithNetty("localhost", 8888).run(command, filename);

        return true;
    }
}