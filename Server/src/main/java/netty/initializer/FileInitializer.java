package netty.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.stream.ChunkedWriteHandler;
import netty.handler.inbound.Distributor;
import netty.handler.inbound.Parser;
import netty.handler.inbound.UploadHandler;
import netty.handler.outbound.DownloadHandler;
import netty.handler.outbound.SenderForServer;
import netty.handler.outbound.TransferDataBuilder;

public class FileInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // Download (outbound)
        pipeline.addLast(new SenderForServer());        // (4) send
        pipeline.addLast(new TransferDataBuilder());    // (3) bulid a TransferData
        pipeline.addLast(new ChunkedWriteHandler());    // (2) chunk
        pipeline.addLast(new DownloadHandler());        // (1) load a file

        // Upload (inbound)
        pipeline.addLast(new Parser());                 // (1) receive data
        pipeline.addLast(new Distributor());            // (2) upload or download
        pipeline.addLast(new UploadHandler());          // (3) file save

    }
}
