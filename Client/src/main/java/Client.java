import netty.FileTransfer;

import java.io.File;
import java.util.Scanner;

public class Client {

    private static final int SIG_QUIT = -1;
    private static final int SIG_IGNORE = 0;
    private static final int SIG_PUT = 1;
    private static final int SIG_GET = 2;

    private static boolean doEncrypt = false;
    private static boolean doCompress = true;
    private static String filePath = null;

    public static void main(String[] args) {
        FileTransfer ft = new FileTransfer("localhost", 8889);
        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.print(">>> ");
            String input = scanner.nextLine().trim();
            if(input.isEmpty()) continue;

            String[] commands = input.split(" ");

            int command = parser(commands);
            if(command == SIG_QUIT) break;

            switch (command) {
                case SIG_PUT: ft.upload(filePath, doEncrypt, doCompress); break;
                case SIG_GET: ft.download(filePath); break;
                case SIG_IGNORE: continue;
                default: System.out.println("메시지 파싱 오류");
            }
        }

        ft.close();
        scanner.close();
        System.out.println("Shutdown Program...");
    }

    private static boolean isFileExist(String filePath) {
        if(filePath == null) return false;
        File file = new File(filePath);
        return file.exists();
    }

    private static int parser(String[] commands) {
        try {

            // quit the program
            if (commands[0].equals("quit")) return SIG_QUIT;

            if(commands.length < 2)
                throw new ArrayIndexOutOfBoundsException();

            // file transfer
            if (commands[0].equals("get")) return SIG_GET;
            if (commands[0].equals("put")) {

                doEncrypt = commands[1].equals("-e");
                filePath = doEncrypt ? commands[2] : commands[1];
                if (isFileExist(filePath)) return SIG_PUT;
            }

            System.out.println("파일을 찾을 수 없습니다.");
            return SIG_IGNORE;

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("명령어를 확인해주세요.");
        }

        return SIG_IGNORE;
    }
}
