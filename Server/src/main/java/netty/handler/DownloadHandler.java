package netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedStream;
import netty.common.FileSpec;
import netty.common.FileUtils;
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
            List<String> list = FileUtils.getFilePathList(filePath); // 파일 목록








//            List<String> list = FileUtils.getFilePathList(filePath); // 파일 목록
//            int listLen = list.size();
//            for(int i=0; i<listLen; i++) {
//                String curFile = list.get(i);
                FileInputStream fis = new FileInputStream(filePath);

                // 메타 데이터 전송
                ObjectInputStream ois = new ObjectInputStream(fis);
                FileSpec fs = (FileSpec) ois.readObject();
                fs.setCurrentFileSize(FileUtils.getSize(filePath));

                // 마지막 파일 확인
//                boolean isLastFile = (i == listLen - 1);
//                fs.setEndOfFileList(isLastFile);

                ctx.writeAndFlush(fs);

                // 파일 데이터 전송
//                long offset = fis.getChannel().position();
//                RandomAccessFile raf = new RandomAccessFile(filePath, "r");
//                ChunkedFile chunkedFile = new ChunkedFile(raf, offset, fs.getCurrentFileSize() - offset, 8192);
//                ctx.writeAndFlush(chunkedFile);

                ChunkedStream chunkedStream = new ChunkedStream(fis);
                ctx.writeAndFlush(chunkedStream);

//            }

        } catch (FileNotFoundException e) {
            logger.warn("No such file or directory");
            ctx.writeAndFlush("파일 또는 폴더를 찾을 수 없습니다.");
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