import netty.FileTransfer;
import netty.common.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Client {

    private static final int SIG_QUIT = -1;
    private static final int SIG_IGNORE = 0;
    private static final int SIG_PUT = 1;
    private static final int SIG_GET = 2;

    private static boolean doEncrypt = false;
    private static boolean doCompress = false;
    private static String filePath = null;

    public static void main(String[] args) throws IOException {
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
                case SIG_PUT:
                    System.out.println("Upload Started.");

                    List<String> list = FileUtils.getFilePathList(filePath); // 파일 목록
                    for(int i=0; i<list.size(); i++) {
                        String curFile = list.get(i);
                        System.out.println("[" + (i+1) + "/" + list.size() + "]" + curFile + " 업로드 중");
                        ft.upload(curFile, doEncrypt, doCompress);
                    }
                    
                    System.out.println("Upload Succeed.");
                    break;

                case SIG_GET:
                    System.out.println("Download Started.");

                    ft.download(filePath);

                    System.out.println("Download Complete.");
                    break;
                case SIG_IGNORE: continue;
                default: System.out.println("메시지 파싱 오류");
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
                filePath = commands[1];
                return SIG_GET;
            }

            // file transfer : upload
            if (commands[0].equals("put")) {

                boolean hasOptions = commands[1].startsWith("-");
                String options = hasOptions ? commands[1].substring(1) : "";

                doEncrypt = options.contains("e");
                doCompress = options.contains("c");
                filePath = hasOptions ? commands[2] : commands[1];

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
