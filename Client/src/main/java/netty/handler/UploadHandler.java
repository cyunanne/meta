package netty.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.stream.ChunkedStream;
import netty.common.FileSpec;
import netty.common.FileUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.net.SocketAddress;
import java.util.List;

public class UploadHandler extends ChannelOutboundHandlerAdapter {

    private FileInputStream fis;
    private ChunkedStream chunkedStream;
    private FileSpec initialFileSpec;
    private boolean isCompressed = false;

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        super.connect(ctx, remoteAddress, localAddress, promise);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        
        // 메타 데이터
        if(msg instanceof FileSpec) {
//            ctx.writeAndFlush(msg);
            initialFileSpec = (FileSpec) msg;
            isCompressed = initialFileSpec.isCompressed();

        }

        // 파일 데이터
        else if(msg instanceof String) {
            String filePath = (String) msg;

            // 압축 정보 전송
            if(isCompressed) {
                FileSpec fs = new FileSpec(filePath);
                fs.setEncrypted(initialFileSpec.isEncrypted())
                        .setCompressed(initialFileSpec.isCompressed());
                ctx.writeAndFlush(fs);
            }


            try {

                synchronized (ctx) { // 파일 순서대로 전송

                    List<String> list = FileUtils.getFilePathList(filePath); // 파일 목록
                    for(int i=0; i<list.size(); i++) {
                        String curFile = list.get(i);
                        System.out.println("[" + (i+1) + "/" + list.size() + "] " + curFile + " 업로드 중");

                        // 개별 파일 정보 전송
                        FileSpec fs = new FileSpec(curFile);
                        fs.setEncrypted(initialFileSpec.isEncrypted())
                                .setCompressed(initialFileSpec.isCompressed());

                        // 마지막 파일 확인
                        boolean isLastFile = (i == list.size() - 1);
                        fs.setEndOfFileList(isLastFile);

                        ctx.writeAndFlush(fs);

                        // 파일 데이터 전송
                        FileInputStream fis = new FileInputStream(curFile);
                        ChunkedStream chunkedStream = new ChunkedStream(fis);
                        ctx.writeAndFlush(chunkedStream);
                    }

                }

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
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.disconnect(ctx, promise);

        if(fis != null) fis.close();
        if(chunkedStream != null) chunkedStream.close();
    }
}