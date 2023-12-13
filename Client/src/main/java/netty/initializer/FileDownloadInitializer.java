package netty.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import netty.handler.inbound.DecompressHandler;
import netty.handler.inbound.DecryptHandler;
import netty.handler.inbound.DownloadHandler;
import netty.handler.inbound.Parser;
import netty.handler.outbound.DownloadReqHandler;
import netty.handler.outbound.Sender;

public class FileDownloadInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // outbound
        pipeline.addLast(new Sender());
        pipeline.addLast(new DownloadReqHandler());

        // inbound
        pipeline.addLast(new Parser());
        pipeline.addLast(new DecryptHandler());    // decrypt
        pipeline.addLast(new DecompressHandler()); // decompress
        pipeline.addLast(new DownloadHandler());
    }
}
