import netty.FileTransfer;

import java.io.File;
import java.util.Scanner;

public class Client {

    private static final int SIG_QUIT = -1;
    private static final int SIG_IGNORE = 0;
    private static final int SIG_PUT = 1;
    private static final int SIG_GET = 2;

    public static void main(String[] args) {
        FileTransfer ft = new FileTransfer("localhost", 8889);
        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.print(">>> ");
            String input = scanner.nextLine().trim();
            if(input.isEmpty()) continue;

            String[] commands = input.split(" ");
            if(commands.length != 2) {
                System.out.println("명령어를 확인해주세요.");
                continue;
            }

            int command = parser(commands);
            if(command == SIG_QUIT) break;

            String filepath = commands[1];
            switch (command) {
                case SIG_PUT: ft.upload(filepath); break;
                case SIG_GET: ft.download(filepath); break;
                case SIG_IGNORE: continue;
                default: System.out.println("메시지 파싱 오류");
            }
        }

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
