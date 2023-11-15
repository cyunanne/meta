package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty._test.FileSpec;
import netty._test.Header;
import netty._test.TransferData;
import netty.cipher.ASE256Cipher;

import javax.crypto.Cipher;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private OutputStream os;

    private FileOutputStream fos;
    private FileSpec fileSpec;
    private Long received = 0L;

    // test
    private ASE256Cipher cipher = new ASE256Cipher(Cipher.DECRYPT_MODE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Client Connected : " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("channelRead~~~");

        os = Files.newOutputStream(Paths.get("my.pdf"));
        os.write((byte[]) msg);



        if ( !(msg instanceof TransferData) ) return;

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf byteBuf = td.getData();

        switch(header.getType()) {
            case Header.TYPE_MSG:
                break;

            case Header.TYPE_META:
                setFileSpec(byteBuf);
                break;

            case Header.TYPE_DATA:

//                byte[] arr = new byte[header.getLength()];
//                byteBuf.readBytes(arr);
//                fos.write(arr);
//                byteBuf.release();

//                if( writeToFile(byteBuf, header) ) break;
//                ctx.writeAndFlush(new TransferData(Header.TYPE_MSG, Header.CMD_PUT, true));
                break;
            default: break;
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
//        System.out.println("channelReadComplete");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
//        closeFile();
        System.out.println("channel closed : " + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        ctx.close();
        cause.printStackTrace();
    }

    private void echoMessage(ChannelHandlerContext ctx, Object msg) throws IOException {
        String message = (String)msg;
        String port = ctx.channel().remoteAddress().toString().split(":")[1];
        System.out.println("Client" + port + " : " + message);

        if (message.startsWith("put")) {
            String filename = message.split(" ")[1];
            os = Files.newOutputStream(Paths.get(filename));
        } else {
            ctx.writeAndFlush(message);
        }
    }

    private void closeFile() {
        System.out.println("file closed");
        try {
            if (os != null) {
                os.flush();
                os.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFileSpec(ByteBuf byteBuf) throws IOException {
        fileSpec = new FileSpec(byteBuf);
        fos = new FileOutputStream(fileSpec.getName());
    }

    private boolean writeToFile(ByteBuf byteBuf, Header header) throws IOException {
        FileChannel fileChannel = fos.getChannel();
        received += fileChannel.write(byteBuf.nioBuffer());
        byteBuf.release();

        System.out.println(fileSpec.getSize() + " / " + received);

        if (header.isEof() && fileSpec.getSize() <= received) {
            fos.close();

            System.out.println("File Closed : " + fileSpec.getName() + " / " + received + " bytes");

            received = 0L;
            fileSpec = null;
            return false;
        }
        return true;
    }
}