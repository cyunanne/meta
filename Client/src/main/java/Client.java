import netty.FileTransfer;
import netty.MessageTransfer;

import java.io.File;
import java.util.Scanner;

public class Client {

    private static final int SIG_QUIT = -1;
    private static final int SIG_IGNORE = 0;
    private static final int SIG_PUT = 1;
    private static final int SIG_GET = 2;
    private static final int SIG_MSG = 3;

    public static void main(String[] args) {
        MessageTransfer mt = new MessageTransfer("localhost", 8888);
        FileTransfer ft = new FileTransfer("localhost", 8889, mt);
        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.print(">>> ");
            String input = scanner.nextLine().trim();
            if(input.isEmpty()) continue;

            String[] commands = input.split(" ");
            int command = parser(commands);
            if(command == SIG_QUIT) break;

            switch (command) {
                case SIG_PUT: ft.upload(commands[1]); break;
                case SIG_GET: ft.download(commands[1]); break;
                default: mt.send(input); break;
            }
        }

        mt.close();
        ft.close();
        scanner.close();
        System.out.println("Shutdown Program...");
    }

    private static boolean isFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    private static int parser(String[] commands) {

        // file transfer
        if(commands[0].equals("get")) return SIG_GET;
        if(commands[0].equals("put")) {
            if(isFileExist(commands[1])) return SIG_PUT;
            System.out.println("파일을 찾을 수 없습니다.");
            return SIG_IGNORE;
        }

        // exceptions
        if(commands[0].equals("quit")) return SIG_QUIT;

        // message
        return SIG_IGNORE;
    }
}
