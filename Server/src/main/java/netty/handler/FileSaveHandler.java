package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.string.StringEncoder;
import netty.common.FileSpec;
import netty.common.Message;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDateTime;

public class FileSaveHandler extends ChannelInboundHandlerAdapter {

    private FileOutputStream fos;
    private FileSpec filespec;

    private Long received = 0L;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("File Channel Connected : " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;

        // 파일 정보 수신
        if(filespec == null) {
            Message header = new Message(byteBuf.readByte());
            int len = header.setLength(byteBuf.readUnsignedShort());
            filespec = new FileSpec(byteBuf.readBytes(len));

            String filePath = filespec.getName();
            switch (header.getCmd()) {

                // upload
                case Message.CMD_PUT:
                    fos = new FileOutputStream(filePath);
//                    ctx.writeAndFlush(new Message(true));
                    break;

                // download
                case Message.CMD_GET:
                    ctx.writeAndFlush(filePath);
                    break;
            }

        // 파일 수신
        } else {
            received += fos.getChannel().write(byteBuf.nioBuffer());
//            fos.getChannel().force(true);
//            System.out.println(received);

            if(filespec.getSize() <= received) {
                ctx.close();
            }
        }

        byteBuf.release();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(fos != null) {
            fos.getChannel().force(true);
            fos.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        cause.printStackTrace();
    }

}