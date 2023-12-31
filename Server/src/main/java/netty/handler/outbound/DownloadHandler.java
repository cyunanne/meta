package netty.handler.outbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.stream.ChunkedStream;
import netty.common.Builder;
import netty.common.FileSpec;
import netty.common.FileUtils;
import netty.common.Header;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.util.List;

public class DownloadHandler extends ChannelOutboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(DownloadHandler.class);
    private FileInputStream fis;
    private ObjectInputStream ois;
    private ChunkedStream chunkedStream;
    private FileSpec fs;
    private String filePath;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {

        try {

            filePath = (String) msg;

            // 디렉터리인 경우
            if(FileUtils.isDirectory(filePath)) {

                // 디렉터리 내부 파일 목록 전송
                List<String> list = FileUtils.getFilePathList(filePath);
                for(String file : list) {
                    ctx.writeAndFlush(Builder.wrap(file));
                }

                // 채널 종료 신호 전달
                ctx.writeAndFlush(Builder.wrap("fin"));
            }

            // 단일파일인 경우
            else {
                fis = new FileInputStream(filePath);
                ois = new ObjectInputStream(fis);
                fs = (FileSpec) ois.readObject();
                fs.setCurrentFileSize(FileUtils.getSize(filePath));
                chunkedStream = new ChunkedStream(fis, Header.CHUNK_SIZE);

                ctx.writeAndFlush(Builder.wrap(fs));
                ctx.writeAndFlush(chunkedStream);
            }

        } catch (FileNotFoundException e) {
            logger.warn("No such file or directory");
            ctx.writeAndFlush(Builder.wrap("error: 파일 또는 폴더를 찾을 수 없습니다."));
            ctx.close();

        } catch (ClassNotFoundException | InvalidClassException e) {
            logger.warn("Can not found class or data changed");
            ctx.close();

        } catch (Exception e) {
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
        if(fs != null) {
            logger.info(String.format("file downloaded: %s (%d bytes)",
                    filePath, FileUtils.getSize(filePath)));
        }
    }

}