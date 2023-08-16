import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

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
                    FileHandler fileHandler = new FileHandler();

                    // SSL
                    SslContext sslContext = SslContextBuilder.forClient().build();
                    pipeline.addLast(sslContext.newHandler(ch.alloc()));

//                    pipeline.addLast(new StringEncoder());
                    pipeline.addLast(new ByteArrayEncoder());
//                    pipeline.addLast(new ByteArrayDecoder());
                    pipeline.addLast(new StringDecoder());

                    pipeline.addLast(new ClientHandler());
                    pipeline.addLast(fileHandler);

                }
            });
            Channel serverChannel = bootstrap.connect(host, port).sync().channel();

//            MyCipher myCipherE = new MyCipher('E');
//            byte[] data = myCipherE.encrypt("testtest".getBytes());
//            System.out.println("encrytped : " + new String(data));
//
//            MyCipher myCipherD = new MyCipher('D');
//            byte[] data2 = myCipherD.decrypt(data);
//            System.out.println("decrytped : " + new String(data2));

            // 메시지 전송
            Scanner scanner = new Scanner(System.in);
            while(serverChannel.isWritable()) {
                String filename = scanner.nextLine();
                if("quit".equals(filename)){
                    serverChannel.closeFuture().sync();
                    break;
                }

                serverChannel.writeAndFlush(filename);
            }

        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}