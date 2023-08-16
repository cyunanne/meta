import io.netty.channel.*;

import javax.crypto.Cipher;
import java.io.*;
import java.util.Arrays;

public class FileHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//        System.out.println("FileHandler visited");
        String filename = (String)msg;

        if(!new File(filename).exists()) {
            System.out.println("존재하지 않는 파일입니다.");
            return;
        }

        Cipher cipher = (new MyCipher('E')).getCipher();
        InputStream input = new BufferedInputStream(new FileInputStream((String)msg));

        byte[] buffer = new byte[1024];
        System.arraycopy(filename.getBytes(), 0, buffer, 0, filename.length());
        ctx.writeAndFlush(buffer);

        int read = -1;
        while ((read = input.read(buffer)) != -1) {
            ctx.write(cipher.update(buffer, 0, read));
        }
        ctx.writeAndFlush(cipher.doFinal()).sync();

        input.close();



//        serverChannel.writeAndFlush("fin");
//        System.out.println("----------fin----------");

        
        
        
        
//        File file = new File((String)msg);
//        if(!file.exists()) {
//            System.out.println("존재하지 않는 파일입니다.");
//        }
//
//        String filename = file.getName();
//        String name = filename.substring(0, filename.lastIndexOf("."));
//        String ext = filename.substring(filename.lastIndexOf(".") + 1);

//        byte[] bytes = Files.readAllBytes(file.toPath());

//        FileInfo fileInfo = new FileInfo(name, ext, bytes.length);

//        ctx.writeAndFlush(fileInfo);
//        ctx.writeAndFlush(String.valueOf(bytes.length));
//        System.out.println("length: " + bytes.length);

//        ctx.writeAndFlush(bytes);
//        ctx.flush();

    }
    
}