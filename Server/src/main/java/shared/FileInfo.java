package shared;

import java.io.Serializable;
import java.util.Arrays;

public class FileInfo implements Serializable {

    private static final long serialVersionUID = 123L;

    private String filename;
    private String type;
    private int size;

    private byte[] key;
    private byte[] iv;

    public FileInfo(String filename) {
        this.filename = filename;
    }

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

    public byte[] getKey() { return key;}

    public void setKey(byte[] key) { this.key = key; }

    public byte[] getIv() { return iv; }

    public void setVi(byte[] vi) { this.iv = iv; }

    @Override
    public String toString() {
        return "FileInfo{" +
                "filename='" + filename + '\'' +
                ", type='" + type + '\'' +
                ", size=" + size +
                ", key=" + Arrays.toString(key) +
                ", iv=" + Arrays.toString(iv) +
                '}';
    }
}
