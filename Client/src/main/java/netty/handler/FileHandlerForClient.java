package netty.handler;

import io.netty.channel.*;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileHandlerForClient extends ChannelOutboundHandlerAdapter {

    private final int BLOCK_SIZE = 1024;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws IOException {
        if(msg instanceof String)
            ctx.writeAndFlush(msg);
        
        String[] commands = ((String) msg).split(" ");
        if(!commands[0].equals("put")) return; // 업르도 요청이 아니면 핸들러 종료
        
        // 파일 업로드
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
            ctx.writeAndFlush("fin");
        } catch (NoSuchFileException e) {
            System.out.println("존재하지 않는 파일입니다.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) is.close();
        }
    }
}