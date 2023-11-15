package netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.io.*;

public class GetFileServerHandler extends ChannelOutboundHandlerAdapter {

    private String filename;
    private InputStream inputStream;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        try {
            if(filename == null) {
                this.filename = (String)msg;
                this.inputStream = new FileInputStream(filename);
            }

            // 파일 전송
            byte[] buffer = new byte[1024];
            int read = -1;
            while ((read = inputStream.read(buffer)) != -1) {
                ctx.writeAndFlush(read);
            }

        } catch (FileNotFoundException e) {
            System.out.println("존재하지 않는 파일입니다.");
//            ReferenceCountUtil.release(msg);

        } finally {
            inputStream.close();
            System.out.println("파일 전송 완료\n");
        }
    }

}
