package netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import javax.crypto.Cipher;
import java.io.*;

public class FileHandler extends ChannelInboundHandlerAdapter {

//    private FileOutputStream fos = ;
    private FileOutputStream fos;
//    private int written = 0;
//    private BufferedWriter writer;
//    byte[] fileBuffer;

    public FileHandler() throws Exception {
//        file = new File("testfile");//remember to change des
//        writer = new BufferedWriter(new FileWriter(file));
       fos = new FileOutputStream("testfile_enc");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel active");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("read");
//        if (!file.exists()) {
//            file.createNewFile();
//        }
//
//        int length = Integer.parseInt((String)msg);
//        System.out.println("length: " + length);




//        if (message.equals("fin")) {
            // 데이터 수신 종료
//            System.out.println("data transfer finished");
//            fos.close();
//
//            decryption("testfile_enc", "testfile");
//        } else {

        // 암호화 파일 저장
        if (fos == null) {
            fos = new FileOutputStream("testfile_enc", true);
        }
        fos.write((byte[]) msg);


//        System.out.println("---------------------------------------------------");
//        String message = new String(((byte[])msg));
//        System.out.println(message);

//        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("channelReadComplete");
//        System.out.println("data transfer finished");

//        if(fos != null)
//        written = 0;
//        ctx.writeAndFlush("File Saved");

        if(ctx.channel().bytesBeforeWritable() == 0) {
            fos.close();

            // 파일 복호화(확인용)
//            decryption("testfile_enc", "testfile");
//            System.out.println("파일 복호화 완료");
        }
    }

    private static void decryption(String src, String des) {
        try {
            InputStream input = new BufferedInputStream(new FileInputStream(src));
            OutputStream output = new BufferedOutputStream(new FileOutputStream(des));

            Cipher cipher = (new MyCipher('D')).getCipher();
            byte[] buffer = new byte[1024];
            int read = -1;
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
