package netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.common.FileSpec;

import java.io.FileNotFoundException;

public class MessageHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Message Channel Connected : " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if(msg instanceof String) {
            System.out.println(">>> " + (String) msg);

        } else {
            FileSpec fs = (FileSpec) msg;
            changeUploadFilePath(fs);
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Message Channel Closed.");
    }

    private void changeUploadFilePath(FileSpec fileSpec) {

    }

}