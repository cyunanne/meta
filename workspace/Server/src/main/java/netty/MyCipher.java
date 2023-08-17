package netty;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * ASE-256 암호화
 */
public class MyCipher {

//    private final String key = "01234567890123456789012345678901"; // 32byte
    private byte[] key;
//    private final String iv = key.substring(0, 16); // 16byte
    private byte[] iv;

    private Cipher cipher;
    private SecretKeySpec keySpec;// = new SecretKeySpec(key.getBytes(), "AES");
    private IvParameterSpec ivParamSpec;// = new IvParameterSpec(iv.getBytes());


    public MyCipher(char ch) throws Exception {
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        keySpec = new SecretKeySpec(key, "AES");
        ivParamSpec = new IvParameterSpec(iv);

        if(ch == 'D')
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);
        else
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public Cipher getCipher() {
        return cipher;
    }
}
