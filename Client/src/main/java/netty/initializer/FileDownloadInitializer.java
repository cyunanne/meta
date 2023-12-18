package netty.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import netty.FileTransfer;
import netty.handler.inbound.DecompressHandler;
import netty.handler.inbound.DecryptHandler;
import netty.handler.inbound.DownloadHandler;
import netty.handler.inbound.Parser;
import netty.handler.outbound.SenderForClient;
import netty.handler.outbound.TransferDataBuilder;

public class FileDownloadInitializer extends ChannelInitializer<SocketChannel> {

    private FileTransfer transfer;

    public FileDownloadInitializer(FileTransfer transfer) {
        this.transfer = transfer;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // outbound
        pipeline.addLast(new SenderForClient());            // (2) send
        pipeline.addLast(new TransferDataBuilder());        // (1) header 추가

        // inbound
        pipeline.addLast(new Parser());                     // (1) TransferData 타입으로 변환
        pipeline.addLast(new DecryptHandler());             // (2) decrypt
        pipeline.addLast(new DecompressHandler());          // (3) decompress
        pipeline.addLast(new DownloadHandler(transfer));    // (4) file save
    }
}
