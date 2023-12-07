package netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.stream.ChunkedStream;
import netty.common.FileSpec;
import netty.common.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

public class DownloadHandler extends ChannelOutboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(Distributor.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {

        try {

            List<String> list = FileUtils.getFilePathList((String) msg); // 파일 목록
            for(int i=0; i<list.size(); i++) {
                String curFile = list.get(i);
                FileInputStream fis = new FileInputStream(curFile);

                // 메타 데이터 전송
                ObjectInputStream ois = new ObjectInputStream(fis);
                FileSpec fs = (FileSpec) ois.readObject();
                fs.setCurrentFileSize(FileUtils.getSize(curFile));

                // 마지막 파일 확인
                boolean isLastFile = (i == list.size() - 1);
                fs.setEndOfFileList(isLastFile);

                ctx.writeAndFlush(fs);

                // 파일 데이터 전송
                ChunkedStream chunkedStream = new ChunkedStream(fis);
                ctx.writeAndFlush(chunkedStream);

                // 스트림 닫기
                fis.close();
                ois.close();
                chunkedStream.close();
            }

        } catch (FileNotFoundException e) {
            ctx.close();
            logger.warn("파일을 찾을 수 없습니다.");
        } catch (IOException e) {
            ctx.close();
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            ctx.close();
            logger.warn("클래스를 찾을 수 없습니다.");
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}