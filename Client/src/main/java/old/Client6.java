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

public class Client6 {

    private static final String host = "localhost";
    private static final int port = 8888;

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {

        while( func() );
    }

    public static boolean func() throws Exception {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Channel channel = null;

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
//                    pipeline.addLast(new FileSendHandler());
                }
            });

//            String filename = scanner.nextLine(); // error
            channel = bootstrap.connect(host, port).sync().channel();

            String filename = scanner.nextLine();
            if ("quit".equals(filename)) {
                channel.close().sync();
                eventLoopGroup.shutdownGracefully();
                return false;
            }
            channel.writeAndFlush(filename);

        } finally {
            if(channel != null) channel.close().sync();
            eventLoopGroup.shutdownGracefully();
        }

        return true;
    }
}