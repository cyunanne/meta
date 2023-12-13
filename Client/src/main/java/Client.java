import netty.FileTransfer;
import netty.common.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Client {

    private static final int SIG_QUIT = -1;
    private static final int SIG_IGNORE = 0;
    private static final int SIG_PUT = 1;
    private static final int SIG_GET = 2;

    private static boolean doEncrypt = false;
    private static boolean doCompress = false;
    private static List<String> filePath;

    public static void main(String[] args) {
        FileTransfer ft = new FileTransfer("localhost", 8889);
        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.print(">>> ");
            String input = scanner.nextLine().trim();
            if(input.isEmpty()) continue;

            filePath = new ArrayList<>();
            String[] commands = input.split(" ");

            int command = parser(commands);
            if(command == SIG_QUIT) break;

            switch (command) {
                case SIG_PUT:
                    System.out.println("Upload Started.");
                    ft.upload(filePath, doEncrypt, doCompress);
                    System.out.println("Upload Succeed.");
                    break;

                case SIG_GET:
                    ft.download(filePath);
                    break;

                case SIG_IGNORE:
                    continue;

                default:
                    System.out.println("메시지 파싱 오류");
            }
        }

        scanner.close();
        System.out.println("Shutdown...");
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

            // file transfer : download
            if (commands[0].equals("get")) {
                filePath.addAll(Arrays.asList(commands).subList(1, commands.length));
                return SIG_GET;
            }

            // file transfer : upload
            if (commands[0].equals("put")) {

                boolean hasOptions = commands[1].startsWith("-");
                String options = hasOptions ? commands[1].substring(1) : "";

                doEncrypt = options.contains("e");
                doCompress = options.contains("c");

                boolean allFileExist = true;
                int startIdx = hasOptions ? 2 : 1;
                for(int i=startIdx; i<commands.length; i++) {
                    filePath.add(commands[i]);
                    if ( !isFileExist(commands[i]) ) allFileExist = false;
                }

                if (allFileExist) return SIG_PUT;
            }

            System.out.println("파일을 찾을 수 없습니다.");
            return SIG_IGNORE;

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("명령어를 확인해주세요.");
        }

        return SIG_IGNORE;
    }
}
