package netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private OutputStream os;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // messages
        if (msg instanceof String) {
            String message = (String) msg;
            System.out.println("Server : " + msg);

            if (message.equals("fin")) {
                System.out.println("파일 업로드 완료");
            } else if (message.equals("fin-d")) {
                System.out.println("파일 다운로드 완료");
                closeFile();
            } else if (message.startsWith("get")) {
                String filename = message.split(" ")[1];
                os = Files.newOutputStream(Paths.get(filename));
            }

            System.out.print(">>> ");

        // files
        } else {
            os.write((byte[]) msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }

    private void closeFile() {
        System.out.println("file closed");
        try {
            if (os != null) {
                os.flush();
                os.close();
//                os = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}