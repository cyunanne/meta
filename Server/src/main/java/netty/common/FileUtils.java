package netty.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {
    public static List<String> getFilePathList(String initialPath) throws IOException {
        List<String> list = new ArrayList<>();
        list.add(initialPath);
        searchRecursively(list, 0);
        return list;
    }

    public static List<String> getFilePathList(List<String> initPathList) throws IOException {
        List<String> list = new ArrayList<>();
        for(String item : initPathList) {
            list.addAll(getFilePathList(item));
        }
        return removeDuplication(list);
    }

    public static List<String> removeDuplication(List<String> input) {
        return input.stream().distinct().collect(Collectors.toList());
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

    public static void rm(String path) throws IOException {
        if( !exist(path) || isDirectory(path) ) return;
            
        try {
            Files.delete(Paths.get(path));
        } catch (IOException e) {
            throw new IOException("파일 삭제 실패");
        }

    }

    public static void rmdir(String path) throws IOException {
        if( !exist(path) || !isDirectory(path) ) return;

        List<String> list = getFilePathList(path);
        for(String filePath : list) {
            rm(filePath);
        }

    }

    public static String rename(String path) {

        String newPath = path;

        if( path.contains("\\") ) {

            int idx = path.indexOf('\\');
            String dir = path.substring(0, idx);
            String rest = path.substring(idx);

            int num = 1;
            while (exist(newPath)) {
                newPath = dir + "(" + (num++) + ")" + rest;
            }

        } else {

            int extensionIdx = path.lastIndexOf(".");
            if(extensionIdx == -1) extensionIdx += path.length();
            String extension = path.substring(extensionIdx);
            String filename = path.substring(0, extensionIdx);

            int num = 1;
            while (exist(newPath)) {
                newPath = filename + "(" + (num++) + ")" + extension;
            }
        }

        return newPath;
    }
}
