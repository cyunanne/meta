package netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.stream.ChunkedStream;
import netty.common.FileSpec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

public class DownloadHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {

        try {
            String filePath = (String) msg;
            FileInputStream fis = new FileInputStream(filePath);

            // 메타 데이터 전송
            ObjectInputStream ois = new ObjectInputStream(fis);
            FileSpec fs = (FileSpec) ois.readObject();
            fs.setCurrentFileSize(new File(filePath).length());
            ctx.writeAndFlush(fs);

            // 파일 데이터 전송
            ChunkedStream chunkedStream = new ChunkedStream(fis);
            ctx.writeAndFlush(chunkedStream);

        } catch (FileNotFoundException e) {
            ctx.close();
            System.out.println("파일을 찾을 수 없습니다.");
        } catch (IOException e) {
            ctx.close();
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            ctx.close();
            System.out.println("클래스를 찾을 수 없습니다.");
            throw new RuntimeException(e);
        }

    }

}