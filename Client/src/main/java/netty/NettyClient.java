package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.initializer.MessageInitializer;
import netty.test.Header;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.instrument.Instrumentation;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class NettyClient {

    protected String host;
    protected int port;
    protected EventLoopGroup eventLoopGroup;
    protected Bootstrap bootstrap;
    protected Channel channel;

    public NettyClient(String host, int port, ChannelInitializer<SocketChannel> ci){
        this.host = host;
        this.port = port;

        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap().group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(ci);
    }

    public NettyClient(String host, int port) {
        this(host, port, new MessageInitializer());
    }

    public Channel getChannel() {
        return channel;
    }

    public void run() {
        try {
            channel = bootstrap.connect(host, port).sync().channel();

            System.out.print(">>> ");
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();

            Header header = new Header('M', command.getBytes(StandardCharsets.UTF_8).length);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(header);

            channel.write(bos.toByteArray());
            channel.writeAndFlush(command).sync();

        } catch(Exception e) {
            e.printStackTrace();
            stop();
        }
    }

    public void stop() {
        channel.close();
        eventLoopGroup.shutdownGracefully();
    }
}
