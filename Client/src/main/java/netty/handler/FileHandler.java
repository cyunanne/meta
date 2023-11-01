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

        String[] commands = ((String) msg).split(" ");
        if(commands[0].equals("put")) {
            filename = commands[1];
            inputStream = Files.newInputStream(Paths.get(filename));
            ctx.writeAndFlush(msg).sync();

            try {

//            MyCipher myCipher = new MyCipher('E');
//            Cipher cipher = myCipher.getCipher();
//
//            // 파일정보 생성
//            FileInfo fileInfo = new FileInfo(filename);
//            fileInfo.setKey(myCipher.getKey());
//            fileInfo.setIv(myCipher.getIv());
//
//            // 파일정보 전송
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            ObjectOutputStream out = new ObjectOutputStream(bos);
//            out.writeObject(fileInfo);
//            ctx.writeAndFlush(bos.toByteArray());
//
//            byte[] buffer = new byte[1024];
//            int read = -1;
//            while ((read = inputStream.read(buffer)) != -1) {
//                ctx.writeAndFlush(cipher.update(buffer, 0, read));
//            }
//            ctx.writeAndFlush(cipher.doFinal());


                byte[] buffer = new byte[1024];
                int read = -1;

                while ((read = inputStream.read(buffer)) != -1) {
//                    if(read < 1024) {
//                        buffer = Arrays.copyOfRange(buffer, 0, read);
//                        ctx.writeAndFlush(buffer).sync();
//                    } else {
                        ctx.writeAndFlush(buffer);
//                    }
                }

                ctx.flush();
                ctx.writeAndFlush("fin").sync();


//            byte[] buffer = new byte[1024];
//            int read = -1;
//
//            while ((read = inputStream.read(buffer)) != -1) {
//                if(read < 1024) {
//                    buffer = Arrays.copyOfRange(buffer, 0, read);
//                    ctx.writeAndFlush(buffer).sync();
//                } else {
//                    ctx.writeAndFlush(buffer);
//                }
//            }

//            System.out.println("파일 전송 완료");

            } catch (FileNotFoundException e) {
                System.out.println("존재하지 않는 파일입니다.");
//            ReferenceCountUtil.release(msg);

            } finally {
                if (inputStream != null) inputStream.close();
//            System.out.println("파일 업로드 완료\n");
            }
        }
    }

}