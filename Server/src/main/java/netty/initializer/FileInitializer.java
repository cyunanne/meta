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
        pipeline.addLast(new MessageEncoder());         // (9) Message -> ByteBuf
        pipeline.addLast(new ChunkedWriteHandler());    // (2) chunk & send
        pipeline.addLast(new FileLoadHandler());        // (1) load a file

        // Upload (inbound)
//        pipeline.addLast(new MessageDecoder());
        pipeline.addLast(new FileSaveHandler());    // file save

    }
}
