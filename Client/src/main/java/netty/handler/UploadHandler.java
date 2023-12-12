package netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.stream.ChunkedStream;
import netty.common.FileSpec;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class UploadHandler extends ChannelOutboundHandlerAdapter {

    private FileInputStream fis;
    private ChunkedStream chunkedStream;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        
        // 메타 데이터
        if(msg instanceof FileSpec) {
            ctx.writeAndFlush(msg);
        }

        // 파일 데이터
        else if(msg instanceof String) {
            String filePath = (String) msg;
            try {

                // 파일 데이터 전송
                fis = new FileInputStream(filePath);
                chunkedStream = new ChunkedStream(fis);
                ctx.writeAndFlush(chunkedStream);

            } catch (FileNotFoundException e) {
                ctx.close();
                System.out.println("파일을 찾을 수 없습니다.");

            } catch (Exception e) {
                ctx.close();
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.close(ctx, promise);
        
        // 스트림 닫기
        if(chunkedStream != null) chunkedStream.close();
        if(fis != null) fis.close();
    }

}