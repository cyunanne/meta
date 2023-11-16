package netty.handler.old;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.common.Header;
import netty.common.TransferData;

import java.io.OutputStream;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private OutputStream os;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if( !(msg instanceof TransferData) ) return;

        TransferData td = (TransferData) msg;
        Header header = td.getHeader();

        switch(header.getType()) {
            case Header.TYPE_MSG:
                if( header.isEof() ) {
                    ctx.close();
                    System.out.println("Server Disconnected.");
                    System.out.print("업로드 성공\n>>> ");
                }
                break;
            case Header.TYPE_META: break;
            case Header.TYPE_DATA: break;
            default: break;
        }


//        // files
//        if (msg instanceof byte[]){
//            byte[] data = (byte[]) msg;
//            os.write(data);
//        }
//
//        // messages
//        else if (msg instanceof String) {
//            String message = (String) msg;
//            System.out.println("Server : " + msg);
//
//            if (message.equals("fin")) {
//                System.out.println("파일 업로드 완료");
//            } else if (message.equals("fin-d")) {
//                System.out.println("파일 다운로드 완료");
//                closeFile();
//            } else if (message.startsWith("get")) {
//                String filename = message.split(" ")[1];
//                os = Files.newOutputStream(Paths.get(filename));
//            } else {
//                System.out.println("?????????");
//            }
//
//            System.out.print(">>> ");
//        }
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