package netty.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.stream.ChunkedStream;
import netty.common.FileSpec;
import netty.common.Message;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.SocketAddress;

public class FileLoadHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        if( !(msg instanceof String) ) {
            ctx.writeAndFlush(msg);
            return;
        }

        String filePath = (String) msg;

        // 파일 정보 전송
        Message header = new Message(Message.CMD_GET);
        header.setData(new FileSpec(filePath).toByteBuf());
        ctx.writeAndFlush(header);

        // 파일 전송
        try {
            FileInputStream fis = new FileInputStream(filePath);



            ChunkedStream chunkedStream = new ChunkedStream(fis);
            ctx.writeAndFlush(chunkedStream).addListener(ChannelFutureListener.CLOSE);

        } catch (FileNotFoundException e) {
            System.out.println("파일을 찾을 수 없습니다.");
            ctx.close();
        }
    }

}