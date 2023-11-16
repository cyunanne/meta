package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.initializer.FileDownloadInitializer;
import netty.initializer.FileUploadInitializer;

public class FileTransfer {

    protected String host;
    protected int port;
    protected EventLoopGroup eventLoopGroup;
    protected Bootstrap bootstrap;

    public FileTransfer(String host, int port) {
        this.host = host;
        this.port = port;

        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap().group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
    }

    public void upload(String filePath) {
        Channel channel = null;

        try {
            bootstrap.handler(new FileUploadInitializer());
            channel = bootstrap.connect(host, port).sync().channel();
            System.out.println("Upload Started.");

            channel.writeAndFlush(filePath);
            channel.closeFuture().sync();

            System.out.println("Upload Succeed.");

        } catch (Exception e) {
            System.out.println(e.getMessage());

        } finally {
            if(channel != null) channel.close();
        }
    }

    public void download(String filePath) {
        Channel channel = null;

        try {
            bootstrap.handler(new FileDownloadInitializer());
            channel = bootstrap.connect(host, port).sync().channel();
            System.out.println("Download Started.");

            channel.closeFuture().sync();

            System.out.println("Download Complete.");

        } catch (Exception e) {
            System.out.println(e.getMessage());

        } finally {
            if(channel != null) channel.close();
        }
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}
