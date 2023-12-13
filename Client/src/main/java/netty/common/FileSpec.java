package netty.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;

import java.io.*;

public class FileSpec implements Serializable {

    private static final long serialVersionUID = 123L;

    private String filePath = "";
    private String newFilePath = "";
    private long originalFileSize = 0L;
    private long currentFileSize = 0L;
    private byte[] key;
    private byte[] iv;
    private boolean endOfFileList = false;
    private boolean encrypted = false;
    private boolean compressed = false;

    public FileSpec() {}

    public FileSpec(FileSpec fs) {
        this.filePath = fs.filePath;
        this.newFilePath = fs.newFilePath;
        this.originalFileSize = fs.originalFileSize;
        this.currentFileSize = fs.currentFileSize;
        this.key = fs.key;
        this.iv = fs.iv;
        this.endOfFileList = fs.endOfFileList;
        this.encrypted = fs.encrypted;
        this.compressed = fs.compressed;
    }

    public FileSpec(String filePath) {
        this.filePath = filePath;
        this.newFilePath = filePath;
        this.originalFileSize = new File(filePath).length();
        this.currentFileSize = this.originalFileSize;
    }

    public FileSpec(ByteBuf byteBuf) {

        try (ByteBufInputStream bis = new ByteBufInputStream(byteBuf);
             ObjectInputStream ois = new ObjectInputStream(bis)) {

            FileSpec fs = (FileSpec) ois.readObject();
            this.filePath = fs.filePath;
            this.newFilePath = fs.newFilePath;
            this.originalFileSize = fs.originalFileSize;
            this.currentFileSize = fs.currentFileSize;
            this.key = fs.key;
            this.iv = fs.iv;
            this.endOfFileList = fs.endOfFileList;
            this.encrypted = fs.encrypted;
            this.compressed = fs.compressed;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public FileSpec(byte[] data) {

        ByteBuf byteBuf = Unpooled.wrappedBuffer(data);
        try (ByteBufInputStream bis = new ByteBufInputStream(byteBuf);
             ObjectInputStream ois = new ObjectInputStream(bis)) {

            FileSpec fs = (FileSpec) ois.readObject();
            this.filePath = fs.filePath;
            this.newFilePath = fs.newFilePath;
            this.originalFileSize = fs.originalFileSize;
            this.currentFileSize = fs.currentFileSize;
            this.key = fs.key;
            this.iv = fs.iv;
            this.endOfFileList = fs.endOfFileList;
            this.encrypted = fs.encrypted;
            this.compressed = fs.compressed;

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);

        } finally {
            byteBuf.release();
        }
    }

    public FileSpec setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getOriginalFileSize() {
        return originalFileSize;
    }

    public boolean isEndOfFileList() {
        return endOfFileList;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public FileSpec setOriginalFileSize(Long originalFileSize) {
        this.originalFileSize = originalFileSize;
        return this;
    }

    public FileSpec setEndOfFileList(Boolean endOfFileList) {
        this.endOfFileList = endOfFileList;
        return this;
    }

    public FileSpec encrypt(Boolean encrypted) {
        this.encrypted = encrypted;
        return this;
    }

    public FileSpec compress(Boolean compressed) {
        this.compressed = compressed;
        return this;
    }

    public byte[] toByteArray() {

        try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos)) {

            out.writeObject(this);
            return bos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ByteBuf toByteBuf() {
        return Unpooled.wrappedBuffer(this.toByteArray());
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getIv() {
        return iv;
    }

    public FileSpec setKey(byte[] key) {
        this.key = key;
        return this;
    }

    public FileSpec setIv(byte[] iv) {
        this.iv = iv;
        return this;
    }

    public long getCurrentFileSize() {
        return currentFileSize;
    }

    public void setCurrentFileSize(long currentFileSize) {
        this.currentFileSize = currentFileSize;
    }

    public String getNewFilePath() {
        return newFilePath;
    }

    public void setNewFilePath(String newFilePath) {
        this.newFilePath = newFilePath;
    }
}
