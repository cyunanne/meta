import java.io.Serializable;

public class FileInfo implements Serializable {

    private String filename;
    private String type;
    private int size;

    public FileInfo(String filename, String type, int size) {
        this.filename = filename;
        this.type = type;
        this.size = size;
    }

    public String getFilename() {
        return filename;
    }

    public String getType() {
        return type;
    }

    public int getSize() {
        return size;
    }
}
