package netty.cipher;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AES256Cipher {

    private byte[] key;
    private byte[] iv;
    private Cipher cipher;

    public AES256Cipher(int mode) {
        this(mode, "01234567890123456789012345678901".getBytes(), "0123456789012345".getBytes());
    }

    public AES256Cipher(int mode, byte[] key, byte[] iv) {
        this.key = key;
        this.iv = iv;

        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(iv);
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(mode, keySpec, ivParamSpec);
        } catch(Exception e) {
            e.printStackTrace();
        }

        System.out.println("cipher created");
    }

    public byte[] update(byte[] bytes) {
        return cipher.update(bytes, 0, bytes.length);
    }

    public byte[] doFinal() throws IllegalBlockSizeException, BadPaddingException {
        return cipher.doFinal();
    }

    public byte[] doFinal(byte[] data) throws IllegalBlockSizeException, BadPaddingException {
        return cipher.doFinal(data);
    }

    public Cipher getCipher() {
        return this.cipher;
    }

    public void clear() {
        this.cipher = null;
    }

    private byte[] generateKey() {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // 키 길이 설정
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return keyGen.generateKey().getEncoded(); // SecretKey -> byte[]
    }

    private byte[] generateIv() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[16];
        secureRandom.nextBytes(keyBytes);
        return keyBytes;
    }

    public byte[] getKey() {
        return this.key;
    }

    public byte[] getIv() {
        return this.iv;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }
}
