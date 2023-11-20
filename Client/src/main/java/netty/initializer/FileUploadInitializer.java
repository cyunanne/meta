package netty.initializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
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

        // outbound
        pipeline.addLast(new MessageEncoder());         // (9) send header
        pipeline.addLast(new ChunkedWriteHandler());    // (2) send
        pipeline.addLast(new Test());                   // byteBuf -> ChunkedStream
        pipeline.addLast(new FileLoadHandler());        // (1) file load & chunk

        // inbound
//        pipeline.addLast(new Test2(filePath));                  //
    }
}
