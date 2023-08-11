import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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

import javax.net.ssl.SSLException;
import java.util.Scanner;

public class Client {

    private static final String host = "localhost";
    private static final int port = 8888;

    public static void main(String[] args) throws Exception {

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            // 채널 생성
            Bootstrap bootstrap = new Bootstrap().group(eventLoopGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    // SSL
//                    SslContext sslContext = SslContextBuilder.forClient().build();
//                    pipeline.addLast(sslContext.newHandler(ch.alloc()));

                    // 암호화
                    pipeline.addLast(new Encryption());
//                    pipeline.addLast(new ByteArrayEncoder());

                    pipeline.addLast(new Decryption());

                    // default
                    pipeline.addLast(new ClientHandler());
                }
            });
            Channel serverChannel = bootstrap.connect(host, port).sync().channel();

            // 메시지 전송
            Scanner scanner = new Scanner(System.in);
            while(serverChannel.isWritable()) {
                String message = scanner.nextLine();
                ByteBuf buf = Unpooled.buffer().writeBytes(message.getBytes());
                serverChannel.writeAndFlush(buf);
//                serverChannel.writeAndFlush(message);

                if("quit".equals(message)){
                    serverChannel.closeFuture().sync();
                    break;
                }
            }

        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    private static SslContext getSslContext() throws SSLException {
        return SslContextBuilder.forClient().build();
    }
}