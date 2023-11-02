package netty.handler;

import io.netty.channel.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileHandler extends ChannelOutboundHandlerAdapter {

    private String filename;
    private InputStream inputStream;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        super.write(ctx, msg, promise);
        super.flush(ctx);

        String[] commands = ((String) msg).split(" ");
        if(commands.length < 2) return;

        if(commands[0].equals("put")) {
            filename = commands[1];
            inputStream = Files.newInputStream(Paths.get(filename));

            try {

                byte[] buffer = new byte[2000];
                int read = -1;

                while ((read = inputStream.read(buffer)) != -1) {
                    if(read < 1024) {
                        buffer = Arrays.copyOfRange(buffer, 0, read);
                        ctx.writeAndFlush(buffer).sync();
                    } else {
                        ctx.writeAndFlush(buffer);
                    }
                }

                ctx.writeAndFlush("fin");

            } catch (FileNotFoundException e) {
                System.out.println("존재하지 않는 파일입니다.");
//            ReferenceCountUtil.release(msg);

            } finally {
                if (inputStream != null) inputStream.close();
//                System.out.println("파일 업로드 완료\n");
            }
        }
    }
}