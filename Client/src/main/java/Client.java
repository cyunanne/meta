import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import java.util.Scanner;

public class Client {

    private static final String host = "localhost";
    private static final int port = 8888;

    public static void main(String[] args) throws Exception {

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap().group(eventLoopGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    // SSL
                    SslContext sslContext = SslContextBuilder.forClient().build();
                    pipeline.addLast(sslContext.newHandler(ch.alloc()));

                    pipeline.addLast(new ByteArrayEncoder());
                    pipeline.addLast(new FileHandler());
                }
            });

            Channel channel = bootstrap.connect(host, port).sync().channel();

            Scanner scanner = new Scanner(System.in);
            String filename = scanner.nextLine();
            if(!"quit".equals(filename)){
                channel.writeAndFlush(filename);
            }

            channel.close().sync();

        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}