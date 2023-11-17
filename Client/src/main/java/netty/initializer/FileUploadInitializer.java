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

    private MessageTransfer messageTransfer;

    public FileUploadInitializer(MessageTransfer mt) {
        this.messageTransfer = mt;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // outbound
        pipeline.addLast(new ChunkedWriteHandler());    // (2) chunk & send
        pipeline.addLast(new FileLoadHandler());        // (1) file load

        // inbound
        pipeline.addLast(new MessageParser(messageTransfer));  // (0) parse
        pipeline.addLast(new MessageHandler()); // (2) print
    }
}
