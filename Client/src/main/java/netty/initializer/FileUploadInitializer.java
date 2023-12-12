package netty.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.stream.ChunkedWriteHandler;
import netty.handler.*;

public class FileUploadInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // inbound
        pipeline.addLast(new Parser());

        // outbound
        pipeline.addLast(new Sender());                 // (6) send
        pipeline.addLast(new EncryptHandler());         // (5) encrypt
        pipeline.addLast(new CompressHandler());        // (4) compress
        pipeline.addLast(new TransferDataBuilder());    // (3) bulid a TransferData
        pipeline.addLast(new ChunkedWriteHandler());    // (2) chunk (ChunkedInput -> ByteBuf)
        pipeline.addLast(new UploadHandler());          // (1) file load
    }
}
