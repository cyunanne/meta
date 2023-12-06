package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import netty.common.FileSpec;
import netty.common.FileUtils;
import netty.common.Header;
import netty.common.TransferData;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class UploadHandler extends ChannelInboundHandlerAdapter {

    private FileOutputStream fos;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("File Channel Connected : " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();
        ByteBuf byteBuf = td.getData();

        // 파일 정보 수신
        if(header.getType() == Header.TYPE_META) {
            FileSpec filespec = new FileSpec(byteBuf);
            String filePath = filespec.getFilePath();

            switch (header.getCmd()) {

                // upload
                case Header.CMD_PUT:
                    FileUtils.mkdir(filePath);
                    fos = new FileOutputStream(filePath);
                    new ObjectOutputStream(fos).writeObject(filespec); // 메타 데이터 저장
                    break;

                // download
                case Header.CMD_GET:
                    ctx.writeAndFlush(filePath);
                    break;
            }
        }

        // 파일 수신
        if(fos != null) {
            fos.getChannel().write(byteBuf.nioBuffer());

            if(header.isEof()) {
                fos.close();
                ctx.close();
            }
        }

        ReferenceCountUtil.release(byteBuf);
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(fos != null) fos.close();
        System.out.println("Channel Closed : " + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        cause.printStackTrace();
    }

}