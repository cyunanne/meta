import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 18000);
            System.out.println("Server 연결됨");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            // write
            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while(socket.isConnected()) {
                    String outputMessage = scanner.nextLine();
                    if (socket.isClosed()) break;
                    out.println(outputMessage);
                    out.flush();
                    if ("quit".equalsIgnoreCase(outputMessage)) break;
                }
            }).start();

            // read
            while(socket.isConnected()) {
                try {
                    String inputMessage = in.readLine();
                    if ("quit".equalsIgnoreCase(inputMessage)) {
                        out.println("quit");
                        out.flush();
                        break;
                    }
                    System.out.println("From Server: " + inputMessage);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (socket.isConnected()) socket.close();
            System.out.println("연결 종료");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}