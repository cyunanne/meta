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
    }

    public void upload(String filePath, boolean doEncrypt, boolean doCompress) {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap().group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new FileUploadInitializer());
        Channel channel = null;

        try {
            channel = bootstrap.connect(host, port).sync().channel();

            // 파일 정보 전송
            FileSpec fs = new FileSpec(filePath);
            fs.setEncrypted(doEncrypt).setCompressed(doCompress);
            channel.writeAndFlush(fs);

            // 파일 전송 (전송완료 후 서버에서 채널 종료)
            channel.writeAndFlush(filePath).addListener(ChannelFutureListener.CLOSE);
            channel.closeFuture().sync();

        } catch (Exception e) {
            System.out.println(e.getMessage());

        } finally {
            if(channel != null) channel.close();
            eventLoopGroup.shutdownGracefully();
        }
    }

    public void download(String filePath) {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap().group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new FileDownloadInitializer());
//        Channel channel = null;

        try {
            Channel channel = bootstrap.connect(host, port).sync().channel();

            // 파일 정보(파일명) 전송
            channel.writeAndFlush(new FileSpec().setFilePath(filePath));
            channel.closeFuture().sync();

        } catch (Exception e) {
            System.out.println(e.getMessage());

        } finally {
//            if(channel != null) channel.close();
            eventLoopGroup.shutdownGracefully();
        }
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}
