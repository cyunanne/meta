package netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import shared.FileInfo;

import javax.crypto.Cipher;
import java.io.*;

public class FileHandler extends ChannelInboundHandlerAdapter {

    private OutputStream outputStream;
    private FileInfo info;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("channel activated");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 파일 정보 생성
        if(info == null) {
            ByteArrayInputStream bis = new ByteArrayInputStream((byte[]) msg);
            ObjectInputStream in = new ObjectInputStream(bis);
            info = (FileInfo) in.readObject();

            // 암호화된 파일에 정보 저장
            outputStream = new FileOutputStream(info.getFilename() + "_enc");
            ObjectOutputStream obs = new ObjectOutputStream(outputStream);
            obs.writeObject(info);
            return;
        }

        outputStream.write((byte[]) msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(outputStream != null) outputStream.close();
        System.out.println("channel closed");

        // 파일 복호화(확인용)
        if(outputStream == null) return;
        decryption(info.getFilename() + "_enc", info.getFilename()/*, info*/);
        System.out.println("파일 복호화 완료");
    }

    private static void decryption(String src, String des/*, FileInfo info*/) {
        try {
            InputStream input = new BufferedInputStream(new FileInputStream(src));
            OutputStream output = new BufferedOutputStream(new FileOutputStream(des));

            ObjectInputStream obs = new ObjectInputStream(input);
            FileInfo info = (FileInfo) obs.readObject();

            MyCipher myCipher = new MyCipher();
            Cipher cipher = myCipher.getCipher();
            byte[] buffer = new byte[1024];
            int read = -1;

            myCipher.setKey(info.getKey());
            myCipher.setIv(info.getIv());
            myCipher.init('D');

            while ((read = input.read(buffer)) != -1) {
                output.write(cipher.update(buffer, 0, read));
            }
            output.write(cipher.doFinal());

            input.close();
            output.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
