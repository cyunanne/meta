package netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws IOException {
        if(msg instanceof String)
            ctx.writeAndFlush(msg);

        // 파일 다운로드
        String[] commands = ((String) msg).split(" ");
        InputStream is = null;
        if(commands[0].equals("get")) {
            try {
                is = Files.newInputStream(Paths.get(commands[1]));
                byte[] buffer = new byte[1024];

                while (is.read(buffer) != -1) {
                    ctx.writeAndFlush(buffer);
                }

                ctx.writeAndFlush("fin-d");
            } catch (FileNotFoundException e) {
                System.out.println("존재하지 않는 파일입니다.");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (is != null) is.close();
            }
        }
    }

}