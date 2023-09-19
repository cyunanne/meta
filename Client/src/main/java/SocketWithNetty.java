import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;

import java.util.Scanner;

public class SocketWithNetty {

    private String host;
    private int port;
    private EventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap;

    private Channel channel;

    public SocketWithNetty(String host, int port){
        this.host = host;
        this.port = port;
        connect();
    }

    public void connect() {
        try {
            eventLoopGroup = new NioEventLoopGroup();
            bootstrap = new Bootstrap().group(eventLoopGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new MainChannelInitializer());

        } catch (Exception e) {
            System.out.println("서버 연결 중 에러 발생");
            e.printStackTrace();
        }
    }

//    private void setHandler(String command) {
//        switch (command) {
//            case "put" -> bootstrap.handler(new PutFileChannelInitializer());
//            case "get" -> bootstrap.handler(new GetFileChannelInitializer());
//        }
//    }

    public void run() throws Exception {

        channel = bootstrap.connect(host, port).sync().channel(); // 서버 연결
        System.out.println("Server Connected");

//        while(true) {
            System.out.print(">>> ");

            Scanner scanner = new Scanner(System.in);
            String str = scanner.nextLine();
//            if (str.equals("quit")) break;

            String[] strings = str.split(" ");
            String command = "", filename = "";
            if(strings.length < 2) {
                command = "";
            } else {
                command = strings[0];
                filename = strings[1];
            }


            channel.writeAndFlush(str).sync();
            switch (command) {
                case "put" -> sendFile(filename);
                case "get" -> receiveFile(filename);
//            case "put" -> new FileSender(host, port+1).run();
//            case "get" -> new FileSender(host, port).run();
//                default ->
            }
//        }

//        System.out.println("프로그램 종료 중");
        channel.close();
        disconnect();
    }

    public void sendFile(String filename) {

        Channel channel2 = null;
        try {
            Bootstrap bootstrap2 = new Bootstrap().group(eventLoopGroup);
            bootstrap2.channel(NioSocketChannel.class);
            bootstrap2.handler(new PutFileChannelInitializer());
            channel2 = bootstrap2.connect(host, port + 1).sync().channel(); // 서버 연결

            //-------------------------------------------------
            System.out.print("\"" + filename +  "\"을 서버에 업로드 합니다. ");
            Scanner scanner = new Scanner(System.in);
            String str = scanner.nextLine();
            //-------------------------------------------------
            // 커넥션 이후에 scanner를 사용하지않으면 에러 발생
            // 원인은 모르겠음... 버스를 공유하나?
            channel2.writeAndFlush(filename);

        } catch(Exception e) {
            System.out.println("파일 전송 중 오류 발생");
        } finally {
            channel2.close();
        }
    }

    public void receiveFile(String filename) {

        Channel channel2 = null;
        try {
            Bootstrap bootstrap2 = new Bootstrap().group(eventLoopGroup);
            bootstrap2.channel(NioSocketChannel.class);
            bootstrap2.handler(new GetFileChannelInitializer());
            channel2 = bootstrap2.connect(host, port + 2).sync().channel(); // 서버 연결

            //-------------------------------------------------
            System.out.print("\"" + filename +  "\"을 서버에서 다운로드 합니다. ");
            Scanner scanner = new Scanner(System.in);
            String str = scanner.nextLine();
            //-------------------------------------------------
            // 커넥션 이후에 scanner를 사용하지않으면 에러 발생
            // 원인은 모르겠음... 버스를 공유하나?

//            System.out.print("filename >>> ");
//            Scanner scanner = new Scanner(System.in);
//            String filename = scanner.nextLine();
//            channel.writeAndFlush(filename).sync();
//            channel2.writeAndFlush(filename);

        } catch(Exception e) {
            System.out.println("파일 전송 중 오류 발생");
        } finally {
            channel2.close();
        }
    }

    public void disconnect() {
        eventLoopGroup.shutdownGracefully();
    }

/*    public void run2(String command) throws Exception {

        Scanner scanner = new Scanner(System.in);
        Channel fileChannel = null;

        try {
            // 명령어 입력
    //                System.out.print("command >>> ");
    //                String command = scanner.nextLine();
    //                if (command.equals("quit")) break;

            setHandler(command); // 명령에 맞는 핸들러 설정
            fileChannel = bootstrap.connect(host, port2).sync().channel(); // 서버 연결

//            // 파일명 입력
//            System.out.print("filename >>> ");
//            String filename = scanner.nextLine();
//            if (!command.equals("quit")) {
//                fileChannel.writeAndFlush(filename);
//                fileChannel.close().sync();
//            }

        } finally{
            if(fileChannel != null && fileChannel.isOpen()) {
                fileChannel.close().sync();
            }
        }
    }*/

}
