package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import netty._test.FileSpec;
import netty._test.Header;
import netty._test.TransferData;

import java.io.*;
import java.nio.channels.FileChannel;

public class _HandlerForServer extends ChannelOutboundHandlerAdapter {

    private FileOutputStream fos;
    private FileSpec fileSpec;
    private Long received = 0L;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws IOException {

        if ( !(msg instanceof TransferData) ) return;

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf byteBuf = td.getData();

        switch(header.getType()) {
            case Header.TYPE_MSG: break;
            case Header.TYPE_META: setFileSpec(byteBuf); break;
            case Header.TYPE_DATA:
                if( writeToFile(byteBuf, header) ) {
                    ctx.writeAndFlush(new TransferData(Header.TYPE_MSG, Header.CMD_PUT, true));
                }
                break;
            default: break;
        }
    }

    private void setFileSpec(ByteBuf byteBuf) throws IOException {
        fileSpec = new FileSpec(byteBuf);
        fos = new FileOutputStream(fileSpec.getName());
    }

    private boolean writeToFile(ByteBuf byteBuf, Header header) throws IOException {
        FileChannel fileChannel = fos.getChannel();
        received += fileChannel.write(byteBuf.nioBuffer());

        if (header.isEof() && fileSpec.getSize() <= received) {
            fos.close();
            received = 0L;
            System.out.println("file closed.");
            return true;
        }
        return false;
    }
}