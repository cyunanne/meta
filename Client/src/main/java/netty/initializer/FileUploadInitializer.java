package netty.initializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.compression.ZstdEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import netty.MessageTransfer;
import netty.common.Message;
import netty.handler.*;

public class FileUploadInitializer extends ChannelInitializer<SocketChannel> {

    private String filePath;

    public FileUploadInitializer(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // outbound : Message
        pipeline.addLast(new MessageEncoder());         // (9) send header (Message -> ByteBuf)

        // outbound : File
        pipeline.addLast(new EncoderTest());            // (4) encrypt
//        pipeline.addLast(new EncoderTest2());           // (3) compress
//        pipeline.addLast(new ZstdEncoder());            // (3) compress

        // TODO 헤더 붙이기..?

        pipeline.addLast(new ChunkedWriteHandler());    // (2) chunk (ChunkedInput -> ByteBuf)
        pipeline.addLast(new FileLoadHandler());        // (1) file load & chunk
    }
}
