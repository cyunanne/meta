package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.stream.ChunkedFile;
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

    int BLOCK_SIZE = Short.MAX_VALUE;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws IOException {
        System.out.println("FileSpec sent.");
        String filename = (String) msg;
        FileSpec fs = new FileSpec(filename);
        ctx.writeAndFlush(new TransferData(fs));

        FileInputStream fis = new FileInputStream(filename);
        byte[] buffer = new byte[BLOCK_SIZE];
        int read = -1;
        while( (read = fis.read(buffer)) != -1 ) {
            boolean eof = ( read < BLOCK_SIZE );
            ctx.writeAndFlush(new TransferData(buffer, Header.CMD_PUT, eof, read));
        }
        fis.close();
    }
}