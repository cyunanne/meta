package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
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

    public void upload(String filePath) {
        Channel channel = null;

        try {
            bootstrap.handler(new FileUploadInitializer(filePath));
            channel = bootstrap.connect(host, port).sync().channel();
            System.out.println("Upload Started.");

            /*// 파일 정보 전송 to Message Channel
            Message msg = new Message(Message.CMD_PUT);
            msg.setData(new FileSpec(filePath).getByteBuf());
            messageTransfer.send(msg);*/

            // 파일 정보 전송 to File Channel
            Message msg = new Message(Message.CMD_PUT);
            msg.setData(new FileSpec(filePath).toByteBuf());
            channel.writeAndFlush(msg);

            // 파일 전송 to File Channel
            channel.writeAndFlush(filePath).addListener(ChannelFutureListener.CLOSE);
//            channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE); // 전송 완료 후 채널 닫기
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

            // 파일 정보(파일명) 전송 to File Channel
            Message msg = new Message(Message.CMD_GET);
            msg.setData(new FileSpec().setName(filePath).toByteBuf());
            channel.writeAndFlush(msg);

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
