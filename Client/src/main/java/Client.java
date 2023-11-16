import netty.FileTransfer;
import netty.MessageTransfer;

import java.io.File;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        FileTransfer ft = new FileTransfer("localhost", 8888);
        MessageTransfer mt = new MessageTransfer("localhost", 8889);
        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.print(">>> ");
            String input = scanner.nextLine();

            int command = parser(input);
            if( command == -1 ) break;
            if( command == 0 ) continue;

            String msg = input.split(" ").length == 1 ?
                    input : input.trim().split(" ")[1];

            switch (command) {
                case 1: mt.send(msg); break;
                case 2: ft.upload(msg); break;
                case 3: ft.download(msg); break;
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

    private static int parser(String command) {

        // file transfer
        if(command.startsWith("put ")) return 2;
        if(command.startsWith("get ")) return 3;

        // exceptions
        if(command.equals("quit")) return -1;
        if(command.trim().isEmpty()) return 0;
        if(!isFileExist(command)) {
            System.out.println("파일을 찾을 수 없습니다.");
            return 0;
        }

        // message
        return 1;
    }
}
