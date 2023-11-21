package netty.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;

import java.io.*;

public class FileSpec implements Serializable {

    private static final long serialVersionUID = 123L;

    private String name = "";
    private Long size = 0L;
    private Boolean endOfFileList = true;
    private Boolean encrypted = false;
    private Boolean compressed = false;

    public FileSpec() {}

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

    public FileSpec(byte[] data) {

        ByteBuf byteBuf = Unpooled.wrappedBuffer(data);
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

        byteBuf.release();
    }

    public FileSpec setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public Long getSize() {
        return size;
    }

    public Boolean isEndOfFileList() {
        return endOfFileList;
    }

    public Boolean isEncrypted() {
        return encrypted;
    }

    public Boolean isCompressed() {
        return compressed;
    }

    public FileSpec setSize(Long size) {
        this.size = size;
        return this;
    }

    public FileSpec setEndOfFileList(Boolean endOfFileList) {
        this.endOfFileList = endOfFileList;
        return this;
    }

    public FileSpec setEncrypted(Boolean encrypted) {
        this.encrypted = encrypted;
        return this;
    }

    public FileSpec setCompressed(Boolean compressed) {
        this.compressed = compressed;
        return this;
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(this);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return bos.toByteArray();
    }

    public ByteBuf toByteBuf() {
        return Unpooled.wrappedBuffer(this.toByteArray());
    }
}
