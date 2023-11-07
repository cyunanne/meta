package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import netty.cipher.ASE256Cipher;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws IOException {
        if(msg instanceof String)
            ctx.writeAndFlush(msg);

        // 파일 업로드
        String[] commands = ((String) msg).split(" ");
        InputStream is = null;
        if(commands[0].equals("put")) {
            try {
                is = Files.newInputStream(Paths.get(commands[1]));
                byte[] buffer = new byte[1024];

                while (is.read(buffer) != -1) {
                    ctx.writeAndFlush(buffer);
                }
                ctx.writeAndFlush("fin");

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