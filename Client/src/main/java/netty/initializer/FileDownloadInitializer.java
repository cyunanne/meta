package netty.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import netty.handler.DecoderTest;
import netty.handler.FileSaveHandler;
import netty.handler.MessageDecoder;
import netty.handler.MessageEncoder;

public class FileDownloadInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // outbound
        pipeline.addLast(new MessageEncoder());

        // inbound
        pipeline.addLast(new MessageDecoder());
        pipeline.addLast(new DecoderTest());
        pipeline.addLast(new FileSaveHandler());
    }
}
