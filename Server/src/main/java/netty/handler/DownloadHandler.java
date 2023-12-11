package netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedStream;
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

                FileInputStream fis = new FileInputStream(filePath);

                // 메타 데이터 전송
                ObjectInputStream ois = new ObjectInputStream(fis);
                FileSpec fs = (FileSpec) ois.readObject();
                fs.setCurrentFileSize(FileUtils.getSize(filePath));
                ctx.writeAndFlush(fs);

                // 파일 데이터 전송
                ChunkedStream chunkedStream = new ChunkedStream(fis);
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

}