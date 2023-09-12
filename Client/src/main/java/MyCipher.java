import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * ASE-256 암호화
 */
public class MyCipher {

    private String key = "01234567890123456789012345678901"; // 32byte
    private String iv = key.substring(0, 16); // 16byte

    private javax.crypto.Cipher cipher;
    private SecretKeySpec keySpec;// = new SecretKeySpec(key.getBytes(), "AES");
    private IvParameterSpec ivParamSpec;// = new IvParameterSpec(iv.getBytes());


    public MyCipher(char ch) throws Exception {
        cipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding");
        keySpec = new SecretKeySpec(key.getBytes(), "AES");
        ivParamSpec = new IvParameterSpec(iv.getBytes());

        if(ch == 'D') cipher.init(javax.crypto.Cipher.DECRYPT_MODE, keySpec, ivParamSpec);
        else          cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);
    }

    public byte[] getKey() { return key.getBytes(); }

    public byte[] getIv() { return iv.getBytes(); }

    public Cipher getCipher() {
        return cipher;
    }
}
