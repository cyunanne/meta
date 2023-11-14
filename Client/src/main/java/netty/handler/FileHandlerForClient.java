package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import netty._test.FileSpec;
import netty._test.Header;
import netty._test.TransferData;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileHandlerForClient extends ChannelOutboundHandlerAdapter {

    int BLOCK_SIZE = 1024;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws IOException {
        String filename = (String) msg;
        File file = new File(filename);

        FileSpec fs = new FileSpec(filename, file.length(), true, false, false);
        ctx.writeAndFlush(new TransferData(fs));

        FileInputStream fis = new FileInputStream(filename);
        byte[] buffer = new byte[BLOCK_SIZE];
        int read = -1;
        while( (read = fis.read(buffer)) != -1 ) {
            ctx.writeAndFlush(new TransferData(buffer, Header.CMD_PUT, false, read));
        }
        fis.close();
    }
}