import java.util.Scanner;

public class Client {

    private static final String host = "localhost";
    private static final int port = 8888;

    public static void main(String[] args) throws Exception {
        new NettyClient(host, port).run();

//        new SocketWithNetty(host, port).run();
//        SocketWithNetty sock = new SocketWithNetty(host, port);
//        sock.connect();
//        while( sock.run() );
//        sock.disconnect();

//        Scanner scanner = new Scanner(System.in);
//        String command = scanner.nextLine();
//        sock.run(command);

//        System.out.println("프로그램 종료");
    }
}