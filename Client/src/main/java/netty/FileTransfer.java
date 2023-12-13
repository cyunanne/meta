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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class FileTransfer {

    private static final Logger logger = LogManager.getLogger(FileTransfer.class);
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

    public void upload(List<String> filePath, boolean doEncrypt, boolean doCompress) {
        init(new FileUploadInitializer());

        try {

            List<String> list = FileUtils.getFilePathList(filePath); // 파일 목록
            List<Channel> channels = new ArrayList<>();

            for(int i=0; i<list.size(); i++) {
                String curFile = list.get(i);

                Channel ch = bootstrap.connect(host, port).sync().channel();
                channels.add(ch);

                FileSpec fs = new FileSpec(curFile);
                fs.setEncrypted(doEncrypt).setCompressed(doCompress);

                // 마지막 파일 확인
                boolean isLastFile = (i == list.size() - 1);
                fs.setEndOfFileList(isLastFile);

                logger.info(String.format("[%d/%d] %s 업로드 시작", i + 1, list.size(), curFile));
                ch.writeAndFlush(fs).addListener(ChannelFutureListener.CLOSE);
            }

            // 채널 종료 대기
            for(Channel ch : channels) {
                ch.closeFuture().sync();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());

        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public void download(List<String> list) {
        list.forEach(this::download);
    }

    public void download(String filePath) {
        init(new FileDownloadInitializer());

        try {
            Channel channel = bootstrap.connect(host, port).sync().channel();

            // 파일 정보(파일명) 전송
            FileSpec fs = new FileSpec().setFilePath(filePath);
            channel.writeAndFlush(fs);
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
