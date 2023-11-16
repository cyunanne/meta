package netty.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;

import java.io.*;

public class FileSpec implements Serializable {

    private static final long serialVersionUID = 123L;

    private final String name;
    private final Long size;
    private final Boolean endOfFileList;
    private final Boolean encrypted;
    private final Boolean compressed;

    public FileSpec(String name, Long size, Boolean endOfFileList, Boolean encrypted, Boolean compressed) {
        this.name = name;
        this.size = size;
        this.endOfFileList = endOfFileList;
        this.encrypted = encrypted;
        this.compressed = compressed;
    }

    public FileSpec(FileSpec fs) {
        this.name = fs.name;
        this.size = fs.size;
        this.endOfFileList = fs.endOfFileList;
        this.encrypted = fs.encrypted;
        this.compressed = fs.compressed;
    }

    public FileSpec(String filename) {
        this.name = filename;
        this.size = new File(filename).length();
        this.endOfFileList = true;
        this.encrypted = false;
        this.compressed = false;
    }

    public FileSpec(ByteBuf byteBuf) {
        FileSpec fs = null;

        try (ByteBufInputStream bis = new ByteBufInputStream(byteBuf);
             ObjectInputStream ois = new ObjectInputStream(bis)) {

            fs = (FileSpec) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        this.name = fs.name;
        this.size = fs.size;
        this.endOfFileList = fs.endOfFileList;
        this.encrypted = fs.encrypted;
        this.compressed = fs.compressed;
    }

    public String getName() {
        return name;
    }

    public Long getSize() {
        return size;
    }

    public Boolean getEndOfFileList() {
        return endOfFileList;
    }

    public Boolean getEncrypted() {
        return encrypted;
    }

    public Boolean getCompressed() {
        return compressed;
    }

    public byte[] getByteArray() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(this);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return bos.toByteArray();
    }

    public ByteBuf getByteBuf() {
        return Unpooled.wrappedBuffer(this.getByteArray());
    }
}
