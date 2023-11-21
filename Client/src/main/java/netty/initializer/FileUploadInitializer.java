package netty.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.stream.ChunkedWriteHandler;
import netty.handler.EncoderTest;
import netty.handler.FileLoadHandler;
import netty.handler.Sender;
import netty.handler.TransferDataBuilder;

public class FileUploadInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // outbound : File
        pipeline.addLast(new Sender());                 // (5) send
        pipeline.addLast(new EncoderTest());            // (4) encrypt
//        pipeline.addLast(new EncoderTest2());         // (3) compress
//        pipeline.addLast(new ZstdEncoder());          // (3) compress
        pipeline.addLast(new TransferDataBuilder());    // bulid a TransferData
        pipeline.addLast(new ChunkedWriteHandler());    // (2) chunk (ChunkedInput -> ByteBuf)
        pipeline.addLast(new FileLoadHandler());        // (1) file load
    }
}
