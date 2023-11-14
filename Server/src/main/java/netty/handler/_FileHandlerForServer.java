package netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class _FileHandlerForServer extends ChannelOutboundHandlerAdapter {

    private final int BLOCK_SIZE = 1024;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws IOException {
        if(msg instanceof String)
            ctx.writeAndFlush(msg);

        String[] commands = ((String) msg).split(" ");
        if(!commands[0].equals("get")) return; // 다운로드 요청이 아니면 핸들러 종료

        // 파일 다운로드
        InputStream is = null;
        try {
            is = Files.newInputStream(Paths.get(commands[1]));
            byte[] buffer = new byte[BLOCK_SIZE];
            int read = -1;

            while ((read = is.read(buffer)) != -1) {
                // 마지막 블록 길이 만큼 자르기
                if(read < BLOCK_SIZE) {
                    buffer = Arrays.copyOfRange(buffer, 0, read);
                }
                ctx.writeAndFlush(buffer);
            }

            ctx.writeAndFlush("fin-d");
        } catch (FileNotFoundException e) {
            ctx.writeAndFlush("존재하지 않는 파일입니다.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) is.close();
        }
    }

}