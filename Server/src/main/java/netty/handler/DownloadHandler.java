package netty.handler;

import io.netty.channel.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedStream;
import io.netty.util.concurrent.FailedFuture;
import io.netty.util.concurrent.FutureListener;
import netty.common.FileSpec;
import netty.common.FileUtils;
import netty.common.Header;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.util.List;

public class DownloadHandler extends ChannelOutboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(DownloadHandler.class);

    private FileInputStream fis;
    private ObjectInputStream ois;
    private ChunkedStream chunkedStream;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {

        try {

            String filePath = (String) msg;

            // 디렉터리인 경우
            if(FileUtils.isDirectory(filePath)) {
                List<String> list = FileUtils.getFilePathList(filePath); // 파일 목록
                for(String file : list) {
                    ctx.writeAndFlush(file);
                }

                // 채널 종료 신호 전달
//                ctx.writeAndFlush(new Header(Header.TYPE_SIG).setFin());
                ctx.writeAndFlush("fin");
            }

            // 단일파일인 경우
            else {

                fis = new FileInputStream(filePath);

                // 메타 데이터 전송
                ois = new ObjectInputStream(fis);
                FileSpec fs = (FileSpec) ois.readObject();
                fs.setCurrentFileSize(FileUtils.getSize(filePath));
                ctx.writeAndFlush(fs);

                // 파일 데이터 전송
                chunkedStream = new ChunkedStream(fis, Header.CHUNK_SIZE);
                ctx.writeAndFlush(chunkedStream);

            }

        } catch (FileNotFoundException e) {
            logger.warn("No such file or directory");
            ctx.writeAndFlush("error: 파일 또는 폴더를 찾을 수 없습니다.");
            ctx.close();

        } catch (ClassNotFoundException e) {
            logger.warn("Can not found class");
            ctx.close();

        } catch (Exception e) {
            ctx.close();
            throw new RuntimeException(e);
        }

    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.close(ctx, promise);
        
        // 스트림 닫기
        if(chunkedStream != null) chunkedStream.close();
        if(ois != null) ois.close();
        if(fis != null) fis.close();
    }

}