package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.common.FileSpec;
import netty.common.FileUtils;
import netty.initializer.FileDownloadInitializer;
import netty.initializer.FileUploadInitializer;

import java.util.ArrayList;
import java.util.List;

public class FileTransfer {

    protected String host;
    protected int port;
    protected EventLoopGroup eventLoopGroup;
    protected Bootstrap bootstrap;

    public FileTransfer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void init(ChannelInitializer<SocketChannel> initializer) {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap().group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(initializer);
    }

    public void upload(String filePath, boolean doEncrypt, boolean doCompress) {
//        eventLoopGroup = new NioEventLoopGroup();
//        bootstrap = new Bootstrap().group(eventLoopGroup);
//        bootstrap.channel(NioSocketChannel.class);
//        bootstrap.handler(new FileUploadInitializer());
        init(new FileUploadInitializer());

        try {

            List<String> list = FileUtils.getFilePathList(filePath); // 파일 목록
            List<Channel> channels = new ArrayList<>();

            for(int i=0; i<list.size(); i++) {

                String curFile = list.get(i);
                System.out.println("[" + (i + 1) + "/" + list.size() + "] " + curFile + " 업로드 중");


                Channel ch = bootstrap.connect(host, port).sync().channel();
                channels.add(ch);

                // 파일 정보 전송
                FileSpec fs = new FileSpec(curFile);
                fs.setEncrypted(doEncrypt).setCompressed(doCompress);
//                channel.writeAndFlush(fs);

                // 마지막 파일 확인
                boolean isLastFile = (i == list.size() - 1);
                fs.setEndOfFileList(isLastFile);

                ch.writeAndFlush(fs);


                // 파일 전송 (전송완료 후 서버에서 채널 종료)
                ch.writeAndFlush(curFile).addListener(ChannelFutureListener.CLOSE);
//                ch.closeFuture().sync();

            }

            for(Channel ch : channels) {
                ch.closeFuture().sync();
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());

        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public void download(String filePath) {
//        eventLoopGroup = new NioEventLoopGroup();
//        bootstrap = new Bootstrap().group(eventLoopGroup);
//        bootstrap.channel(NioSocketChannel.class);
//        bootstrap.handler(new FileDownloadInitializer());
        init(new FileDownloadInitializer());

        try {
            Channel channel = bootstrap.connect(host, port).sync().channel();

            // 파일 정보(파일명) 전송
            channel.writeAndFlush(new FileSpec().setFilePath(filePath));
            channel.closeFuture().sync();

        } catch (Exception e) {
            System.out.println(e.getMessage());

        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}
