package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.common.FileSpec;
import netty.common.Message;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDateTime;

public class FileSaveHandler extends ChannelInboundHandlerAdapter {

    private FileOutputStream fos;

    private FileSpec filespec;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if(msg instanceof FileSpec) {
            filespec = (FileSpec) msg;

        } else if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;

            // 파일 정보 수신
            if (filespec == null) {
                Message header = new Message(byteBuf.readByte());
                int len = header.setLength(byteBuf.readUnsignedShort());
                filespec = new FileSpec(byteBuf.readBytes(len));

                String filePath = filespec.getName();
                switch (header.getCmd()) {

                    // download
                    case Message.CMD_GET:
                        fos = new FileOutputStream(filePath);
                        break;
                }
            }

            // 파일 저장
            if (fos != null) {
                fos.getChannel().write(byteBuf.nioBuffer());
            }
            byteBuf.release();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(fos != null) fos.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        cause.printStackTrace();
    }

}