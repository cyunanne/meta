package netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import netty.common.Message;

public class FileHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {

        /*if(msg instanceof String) {
            String filePath = (String) msg;

            try {
                FileInputStream fis = new FileInputStream(filePath);
                ChunkedStream chunkedStream = new ChunkedStream(fis);
                ctx.writeAndFlush(chunkedStream).addListener(ChannelFutureListener.CLOSE);

            } catch (FileNotFoundException e) {
                System.out.println("파일을 찾을 수 없습니다.");
            }
        } else */if(msg instanceof Message) {
            ctx.writeAndFlush(msg);
        }
    }
}