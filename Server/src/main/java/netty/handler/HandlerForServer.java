package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import netty._test.FileSpec;
import netty._test.Header;
import netty._test.TransferData;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class HandlerForServer extends ChannelOutboundHandlerAdapter {

    private FileOutputStream fos;
    private FileSpec fileSpec;
    private Long received;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws IOException {

        if ( !(msg instanceof TransferData) ) return;

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf byteBuf = td.getData();

        switch(header.getType()) {
            case Header.TYPE_MSG: break;
            case Header.TYPE_META: setFileSpec(byteBuf); break;
            case Header.TYPE_DATA: writeToFile(byteBuf, header); break;
            default: break;
        }
    }

    private void setFileSpec(ByteBuf byteBuf) throws IOException {
        fileSpec = new FileSpec(byteBuf);
        fos = new FileOutputStream(fileSpec.getName());
    }

    private void writeToFile(ByteBuf byteBuf, Header header) throws IOException {
        FileChannel fileChannel = fos.getChannel();
        received += fileChannel.write(byteBuf.nioBuffer());

//        if (header.isEof() && header.getLength() <= received) {
//            fos.close();
//        }
    }
}