package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.common.Message;
import netty.initializer.MessageInitializer;

import java.nio.charset.Charset;

public class MessageTransfer {

    protected String host;
    protected int port;
    protected EventLoopGroup eventLoopGroup;
    protected Bootstrap bootstrap;

    private Channel channel;

    public MessageTransfer(String host, int port) {
        this.host = host;
        this.port = port;

        try {
            eventLoopGroup = new NioEventLoopGroup();
            bootstrap = new Bootstrap().group(eventLoopGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new MessageInitializer());
            channel = bootstrap.connect(host, port).sync().channel();

            System.out.println("Server Connected.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 문자열 전송
     * @param msg
     */
    public void send(String msg) {
        try {
            Message message = new Message(Message.TYPE_HEADER);
            message.setData(Unpooled.copiedBuffer(msg, Charset.defaultCharset()));
            this.send(message);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Message 타입 전송
     * @param msg
     */
    public void send(Message msg) {
        try {
            channel.writeAndFlush(msg);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void close() {
        if(channel != null) channel.close();
        eventLoopGroup.shutdownGracefully();
    }
}
