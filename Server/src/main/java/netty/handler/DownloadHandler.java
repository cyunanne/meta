package netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.stream.ChunkedStream;
import netty.common.FileSpec;
import netty.common.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

public class DownloadHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {

        try {

            String filePath = (String) msg;
            List<String> list = FileUtils.getFilePathList(filePath); // 파일 목록
            for(int i=0; i<list.size(); i++) {
                String curFile = list.get(i);
                FileInputStream fis = new FileInputStream(curFile);

                // 메타 데이터 전송
                ObjectInputStream ois = new ObjectInputStream(fis);
                FileSpec fs = (FileSpec) ois.readObject();
                fs.setCurrentFileSize(new File(curFile).length());

                // 마지막 파일 확인
                boolean isLastFile = (i == list.size() - 1);
                fs.setEndOfFileList(isLastFile);

                ctx.writeAndFlush(fs);

                // 파일 데이터 전송
                ChunkedStream chunkedStream = new ChunkedStream(fis);
                ctx.writeAndFlush(chunkedStream);
            }

            // 스트림 닫기
//            fis.close();
//            ois.close();
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