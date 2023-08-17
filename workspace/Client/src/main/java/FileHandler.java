import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;

import javax.crypto.Cipher;
import java.io.*;
import java.util.Arrays;

public class FileHandler extends ChannelOutboundHandlerAdapter {

    private String filename;
    private InputStream inputStream;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        try {
            if(filename == null) {
                this.filename = (String)msg;
                this.inputStream = new FileInputStream(filename);
            }

            Cipher cipher = (new MyCipher('E')).getCipher();
            byte[] buffer = new byte[Integer.MAX_VALUE-16];

            int read = -1;
            while ((read = inputStream.read(buffer)) != -1) {
                ctx.write(cipher.update(buffer, 0, read));
            }
            ctx.writeAndFlush(cipher.doFinal()).sync();

        } catch (FileNotFoundException e) {
            System.out.println("존재하지 않는 파일입니다.");
            ReferenceCountUtil.release(msg);

        } finally {
            inputStream.close();
            System.out.println("파일 업로드 완료");
        }
    }

}