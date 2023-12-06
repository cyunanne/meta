package netty.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.stream.ChunkedWriteHandler;
import netty.handler.*;

public class FileInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // Download (outbound)
        pipeline.addLast(new Sender());                 // (5) send
        pipeline.addLast(new TransferDataBuilder());    // bulid a TransferData
        pipeline.addLast(new ChunkedWriteHandler());    // (2) chunk
        pipeline.addLast(new DownloadHandler());        // (1) load a file

        // Upload (inbound)
        pipeline.addLast(new Parser());
        pipeline.addLast(new Distributor());
        pipeline.addLast(new UploadHandler());    // file save

    }
}
