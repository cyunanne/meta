package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.common.FileSpec;
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

            // 파일 정보 전송
            channel.writeAndFlush(new FileSpec(filePath));

            // 파일 전송 (전송완료 후 서버에서 채널 종료)
            channel.writeAndFlush(filePath).addListener(ChannelFutureListener.CLOSE);
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

            // 파일 정보(파일명) 전송
            channel.writeAndFlush(new FileSpec().setName(filePath));
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
