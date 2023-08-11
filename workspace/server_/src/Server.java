import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(18000);
            System.out.println("[Server실행]");

            while(true) {
                System.out.println("\nClient 연결 대기중...");
                Socket socket = serverSocket.accept();

                System.out.println("Client 연결됨");
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream());

                // write
                new Thread(() -> {
                    Scanner scanner = new Scanner(System.in);
                    while (socket.isConnected()) {
                        String outputMessage = scanner.nextLine();
                        out.println(outputMessage);
                        out.flush();
                        if ("quit".equalsIgnoreCase(outputMessage)) break;
                    }
                }).start();

                // read
                while (socket.isConnected()) {
                    try {
                        String inputMessage = in.readLine();
                        if ("quit".equalsIgnoreCase(inputMessage)) {
                            out.println("quit");
                            out.flush();
                            break;
                        }
                        System.out.println("From Client: " + inputMessage);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                if(socket.isConnected()) socket.close();
                System.out.println("연결 종료");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();		// ServerSocket 닫기
            } catch (IOException e) {
                System.out.println("소켓통신에러");
            }
        }
    }
}