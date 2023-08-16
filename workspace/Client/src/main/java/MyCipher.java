import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * ASE-256 암호화
 */
public class MyCipher {

    private final String key = "01234567890123456789012345678901"; // 32byte
    private final String iv = key.substring(0, 16); // 16byte

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

    public Cipher getCipher() {
        return cipher;
    }

//    public byte[] encrypt(byte[] data) throws Exception {
//        System.out.println("Encryption");
//
//        int offset = 0;
//        int left = data.length;
//        int len = left > 16 ? 16 : data.length;
//        byte[] result = new byte[left + 16];
//
//        while( left > len ) {
//            cipher.update(data, offset, len, result);
//            left -= len;
//            offset += len;
//        }
//        cipher.doFinal(data, offset, left, result);
//        return result;
//    }
//
//    public byte[] decrypt(byte[] data) throws Exception {
//        System.out.println("Decryption");
//
//        int offset = 0;
//        int left = data.length;
//        int len = 16;
//        byte[] result = new byte[256];
//
//        while( left > len ) {
//            cipher.update(data, offset, len, result);
//            left -= len;
//            offset += len;
//        }
//        cipher.doFinal(data, offset, len, result);
//        return result;
//    }
}
