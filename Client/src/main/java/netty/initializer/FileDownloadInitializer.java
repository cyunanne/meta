package netty.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import netty.handler.*;

public class FileDownloadInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // outbound
        pipeline.addLast(new Sender());
        pipeline.addLast(new TransferDataBuilder());

        // inbound
        pipeline.addLast(new Parser());
        pipeline.addLast(new DecryptHandler());  // decrypt
        pipeline.addLast(new DecompressHandler()); // decompress
        pipeline.addLast(new DownloadHandler());
    }
}
