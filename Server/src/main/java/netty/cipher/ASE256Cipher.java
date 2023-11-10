package netty.cipher;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ASE256Cipher {

    private final String key = "01234567890123456789012345678901"; // 32byte
    private final String iv = key.substring(0, 16); // 16byte

    private Cipher cipher;
    private SecretKeySpec keySpec;// = new SecretKeySpec(key.getBytes(), "AES");
    private IvParameterSpec ivParamSpec;// = new IvParameterSpec(iv.getBytes());


    public ASE256Cipher(int mode) {
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            keySpec = new SecretKeySpec(key.getBytes(), "AES");
            ivParamSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(mode, keySpec, ivParamSpec);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] update(byte[] bytes, int i, int i1) {
        return cipher.update(bytes, i, i1);
    }

    public byte[] doFinal() {
        byte[] result = null;
        try {
            result = cipher.doFinal();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
