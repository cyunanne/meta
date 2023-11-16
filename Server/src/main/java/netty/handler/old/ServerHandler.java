package netty.handler.old;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private OutputStream os;
    private FileOutputStream fos;
//    private ASE256Cipher cipher = new ASE256Cipher(Cipher.DECRYPT_MODE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws FileNotFoundException {
        System.out.println("Client Connected : " + ctx.channel().remoteAddress());
        fos = new FileOutputStream("test2.pdf");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if( !(msg instanceof ByteBuf) ) return;

        ByteBuf byteBuf = (ByteBuf) msg;
        fos.getChannel().write(byteBuf.nioBuffer());
        byteBuf.release();

/*
        // receive chunked data
        if (msg instanceof ByteBuf) {
//            // HeaderAppendedChunkedInput으로 변환하여 데이터 처리
//
            ByteBuf byteBuf = (ByteBuf) msg;
            Header header = new Header();
//
            if (!headerReceived) {
                // 헤더 처리
                header.setHeader(byteBuf.readByte()).setLength(byteBuf.readUnsignedShort());
                headerReceived = true;

            } else {
                InputStream inputStream = new ByteBufInputStream(byteBuf);
                ReadableByteChannel channel = Channels.newChannel(inputStream);

                ChunkedFileWithH chunkedFile = new ChunkedFileWithH(
                        new ChunkedNioStream(channel), // 실제 데이터
                        header // 헤더 데이터 (이미 헤더는 받은 상태)
                );

                // 파일로 데이터 저장
                fos = new FileOutputStream("test99.pdf");
                while (!chunkedFile.isEndOfInput()) {
                    ByteBuf chunk = chunkedFile.readChunk(ctx);
                    byte[] bytes = new byte[chunk.readableBytes()];
                    chunk.readBytes(bytes);
                    fos.write(bytes);
                }

                // 파일 저장 후 닫기
                fos.close();
            }
//
            byteBuf.release();

//            chunkedFile.readChunk(ctx);

            // 여기서부터는 headerAppendedChunkedInput을 이용하여 데이터를 처리할 수 있습니다.
            // 예를 들어, headerAppendedChunkedInput.readChunk()를 사용하여 Chunk 단위로 데이터를 읽어올 수 있습니다.
        }
        // 이후에 데이터를 처리하는 로직을 작성하세요.





//        System.out.println("channelRead~~~");
//
//        ByteBuf buf = (ByteBuf) msg;
//
//        if( msg instanceof FileSpec ) {
//            fileSpec = new FileSpec(buf);
//
//        } else if( msg instanceof ByteBuf ) {
//            received += buf.readableBytes();
//            System.out.println("received : " + received);
//        }
//        buf.release();


//        ByteBuf byteBuf = (ByteBuf) msg;
//        Header header = new Header();
//        header.setHeader(byteBuf.readByte()).setLength(byteBuf.readUnsignedShort());

        if (msg instanceof ChunkedInput) {
            System.out.println("이게될까?");
//            ByteBuf byteBuf = (ByteBuf) msg;
//            fos.getChannel().write(byteBuf.nioBuffer());
//            byteBuf.release();
        }

//        if ( !(msg instanceof TransferData) ) return;
//
//        TransferData td = (TransferData) msg;
//        Header header = td.getHeader();
//        ByteBuf data = td.getData();
//
//        received += header.getLength();
//        System.out.println("received : " + received);
//        td.delete(); // data.release()
//
//        switch(header.getType()) {
//            case Header.TYPE_MSG:
//                ctx.writeAndFlush(td);
//                break;
//
//            case Header.TYPE_META:
//                setFileSpec(data);
//                fileChannel.writeAndFlush(fileSpec);
//                break;
//
//            case Header.TYPE_DATA:
//                if( writeToFile(data, header) ) break;
//                ctx.writeAndFlush(new TransferData(Header.TYPE_MSG, Header.CMD_PUT, true));
//                break;
//            default: break;
//        }*/
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
//        System.out.println("channelReadComplete");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws IOException {
        closeFile();
        if (fos != null) {
            fos.close();
        }
        System.out.println("channel closed : " + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
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

//    private void setFileSpec(ByteBuf byteBuf) throws IOException {
//        fileSpec = new FileSpec(byteBuf);
////        fos = new FileOutputStream(fileSpec.getName());
//    }
//
//    private boolean writeToFile(ByteBuf byteBuf, Header header) throws IOException {
//        FileChannel fileChannel = fos.getChannel();
//        fileChannel.write(byteBuf.nioBuffer());
//        fileChannel.force(true); // 파일에 쓰기
//
//        ReferenceCountUtil.release(byteBuf);
//
////        System.out.println("in handler : " + byteBuf.refCnt());
////        System.out.println("readerIndex : " + byteBuf.readerIndex());
//
//        received += header.getLength();
//        System.out.println(fileSpec.getSize() + " / " + received);
//
//        if (header.isEof() && fileSpec.getSize() <= received) {
//            fos.close();
//
//            System.out.println("File Closed : " + fileSpec.getName() + " / " + received + " bytes");
//
//            received = 0L;
//            fileSpec = null;
//            return false;
//        }
//        return true;
//    }
}