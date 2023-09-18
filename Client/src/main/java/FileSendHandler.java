import io.netty.channel.*;
import shared.FileInfo;

import javax.crypto.Cipher;
import java.io.*;

public class FileSendHandler extends ChannelOutboundHandlerAdapter {

    private String filename;
    private InputStream inputStream;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        try {
            if(filename == null) {
                this.filename = (String)msg;
                this.inputStream = new FileInputStream(filename);
            }

            MyCipher myCipher = new MyCipher('E');
            Cipher cipher = myCipher.getCipher();

            // 파일정보 생성
            FileInfo fileInfo = new FileInfo(filename);
            fileInfo.setKey(myCipher.getKey());
            fileInfo.setIv(myCipher.getIv());

            // 파일정보 전송
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(fileInfo);
            ctx.writeAndFlush(bos.toByteArray()).sync();

            byte[] buffer = new byte[1024];
            int read = -1;
            while ((read = inputStream.read(buffer)) != -1) {
                ctx.writeAndFlush(cipher.update(buffer, 0, read));
            }
            ctx.writeAndFlush(cipher.doFinal()).sync();

        } catch (FileNotFoundException e) {
            System.out.println("존재하지 않는 파일입니다.");
//            ReferenceCountUtil.release(msg);

        } finally {
            inputStream.close();
            System.out.println("파일 업로드 완료\n");
        }
    }

}