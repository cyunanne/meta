package netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import javax.crypto.Cipher;
import java.io.*;
import java.util.Arrays;

public class FileHandler extends ChannelInboundHandlerAdapter {

    private OutputStream outputStream;

    public FileHandler() throws Exception {
        outputStream = new FileOutputStream("testfile_enc");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("channel active");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        outputStream.write((byte[]) msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
//        System.out.println("channelReadComplete");
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        outputStream.close();

        // 파일 복호화(확인용)
        decryption("testfile_enc", "testfile");
        System.out.println("파일 복호화 완료");
    }

    private static void decryption(String src, String des) {
        try {
            InputStream input = new BufferedInputStream(new FileInputStream(src));
            OutputStream output = new BufferedOutputStream(new FileOutputStream(des));

            MyCipher myCipher = new MyCipher('D');
            Cipher cipher = myCipher.getCipher();
            byte[] buffer = new byte[1024];
            int read = -1;

            byte[] key = new byte[32];
            input.read(key, 0, 32);
            myCipher.setKey(key);

            byte[] iv = new byte[16];
            input.read(iv, 0, 16);
            myCipher.setIv(iv);
            
//            System.out.println("key: " + new String(key));
//            System.out.println("iv: " + new String(iv));

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
