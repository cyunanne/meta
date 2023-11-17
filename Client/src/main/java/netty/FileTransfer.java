package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.common.FileSpec;
import netty.common.Message;
import netty.initializer.FileDownloadInitializer;
import netty.initializer.FileUploadInitializer;

public class FileTransfer {

    protected String host;
    protected int port;
    protected EventLoopGroup eventLoopGroup;
    protected Bootstrap bootstrap;

    private MessageTransfer messageTransfer;

    public FileTransfer(String host, int port, MessageTransfer mt) {
        this.host = host;
        this.port = port;
        this.messageTransfer = mt;

        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap().group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
    }
    
//    파일 전송 중간에 메시지 전송 채널을 바꿀 일이 있을까? 없을듯
/*    public FileTransfer(String host, int port) {
        this(host, port, null);
    }

    public void setMessageTransfer(MessageTransfer messageTransfer) {
        this.messageTransfer = messageTransfer;
    }*/

    public void upload(String filePath) {
        Channel channel = null;

        try {
            bootstrap.handler(new FileUploadInitializer(messageTransfer));
            channel = bootstrap.connect(host, port).sync().channel();
            System.out.println("Upload Started.");

            // 파일 정보 전송 to Message Channel
            Message msg = new Message(Message.CMD_PUT);
            msg.setData(new FileSpec(filePath).getByteBuf());
            messageTransfer.send(msg);

            // 파일 전송 to File Channel
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
