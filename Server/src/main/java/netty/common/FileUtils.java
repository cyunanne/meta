package netty.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    public static List<String> getFilePathList(String initialPath) throws IOException {
        List<String> list = new ArrayList<>();
        list.add(initialPath);
        searchRecursively(list, 0);
        return list;
    }

    private static void searchRecursively(List<String> list, int startIdx) throws IOException {

        for(int i=startIdx; i<list.size(); i++) {
            String file = list.get(i);
            if( !isDirectory(file) ) continue;

            Files.newDirectoryStream( Paths.get(file) )
                    .iterator()
                    .forEachRemaining(e -> list.add(e.toString()));

            list.remove(file);
            searchRecursively(list, i);
        }

    }

    public static boolean isDirectory(String path) {
        return Files.isDirectory( Paths.get(path) );
    }

    public static boolean exist(String path) {
        return Files.exists(Paths.get(path));
    }

    public static long getSize(String path) throws IOException {
        return Files.size(Paths.get(path));
    }

    public static void mkdir(String filePath) throws IOException {
        if( !filePath.contains("\\") ) return;

        String dirPath = filePath.substring(0, filePath.lastIndexOf('\\'));
        Path dir = Paths.get(dirPath);

        try {
            Files.createDirectories(dir); // 하위 디렉터리까지 일괄 생성
        } catch (IOException e) {
            throw new IOException("폴더 생성 실패");
        }
    }
}