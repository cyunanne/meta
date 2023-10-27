import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;

public class FileSender extends NettyClient {

    private String filename;

    public FileSender(String host, int port, String filename) throws Exception {
        super(host, port, new PutFileChannelInitializer());
        this.filename = filename;
    }

    @Override
    public void run() {
        try {
            channel = bootstrap.connect(host, port).sync().channel();
            channel.writeAndFlush(filename).sync();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            channel.close();
            eventLoopGroup.shutdownGracefully();
        }
    }

}
