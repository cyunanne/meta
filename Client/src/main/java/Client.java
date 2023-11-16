import netty.FileTransfer;
import netty.MessageTransfer;

import java.io.File;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        MessageTransfer mt = new MessageTransfer("localhost", 8888);
        FileTransfer ft = new FileTransfer("localhost", 8889);
        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.print(">>> ");
            String input = scanner.nextLine().trim();
            if(input.isEmpty()) continue;

            String[] commands = input.split(" ");
            int command = parser(commands);
            if( command == -1 ) break;

            switch (command) {
                case 1: ft.upload(commands[1]); break;
                case 2: ft.download(commands[1]); break;
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
        if(commands[0].equals("put")) {
            if(isFileExist(commands[1])) return 1;
            System.out.println("파일을 찾을 수 없습니다.");
            return 0;
        }
        if(commands[0].equals("get")) {
            if(isFileExist(commands[1])) return 2;
            System.out.println("파일을 찾을 수 없습니다.");
            return 0;
        }

        // exceptions
        if(commands[0].equals("quit")) return -1;

        // message
        return 0;
    }
}
