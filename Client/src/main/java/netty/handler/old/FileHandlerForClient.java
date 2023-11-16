package netty.handler.old;

import io.netty.channel.*;
import io.netty.handler.stream.ChunkedStream;
import netty.common.FileSpec;
import netty.common.Header;
import netty.common.TransferData;

import java.io.*;
import java.nio.channels.FileChannel;

public class FileHandlerForClient extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws IOException {
        String filePath = (String) msg;
        FileInputStream fis = new FileInputStream(filePath);
        ChunkedStream chunkedStream = new ChunkedStream(fis);
        ctx.writeAndFlush(chunkedStream).addListener(ChannelFutureListener.CLOSE);
        System.out.print("파일 업로드 완료\n>>> ");

//        ChunkedInput<ByteBuf> fileChunkedInput = new ChunkedNioFile(fileChannel);
//
//        Header headerData = new Header(Header.TYPE_DATA, Header.CMD_PUT, false);
//        HeaderAppendedChunkedInput headerAppendedChunkedInput = new HeaderAppendedChunkedInput(fileChunkedInput, headerData);

//        ctx.writeAndFlush(headerAppendedChunkedInput);
    }
}