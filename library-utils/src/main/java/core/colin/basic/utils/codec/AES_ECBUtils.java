package core.colin.basic.utils.codec;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密解密（ECB模式）
 *
 * @author PengLin
 * @project robo
 * @desc
 * @date 2019/11/14
 */
public class AES_ECBUtils {
    //"算法/模式/补码方式" , ECB加密模式
    private static final String KEY_ALGORITHM = "AES/ECB/PKCS5Padding";

    // AES加密
    public static byte[] Encrypt(byte[] sSrc, String sKey) throws Exception {
        // 判断Key是否为16位
        if (sKey == null || sKey.length() != 16) {
            return null;
        }
        byte[] raw = sKey.getBytes("UTF-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        return cipher.doFinal(sSrc);

    }

    // AES加密
    public static String Encrypt(String sSrc, String sKey) throws Exception {
        // 判断Key是否为16位
        if (sKey == null || sKey.length() != 16) {
            return null;
        }
        byte[] raw = sKey.getBytes("UTF-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("UTF-8"));
        //此处使用BASE64做转码功能，同时能起到2次加密的作用。
        return Base64Utils.encodeToString(encrypted);
    }

    // AES解密
    public static byte[] Decrypt(byte[] sSrc, String sKey) throws Exception {
        // 判断Key是否为16位
        if (sKey == null || sKey.length() != 16) {
            return null;
        }
        byte[] raw = sKey.getBytes("UTF-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return cipher.doFinal(sSrc);
    }

    // 解密
    public static String Decrypt(String sSrc, String sKey) throws Exception {
        //先用base64解密
        byte[] srcData = Base64Utils.decodeToBytes(sSrc);
        byte[] originalData = Decrypt(srcData, sKey);
        if (originalData == null) return null;

        return new String(originalData, "UTF-8");
    }

    public static void main(String[] args) throws Exception {
        /*
         * 此处使用AES-128-ECB加密模式，key需要为16位。
         */
        String cKey = "1234567890123456";
        // 需要加密的字串
        String cSrc = "www.gowhere.so";
        System.out.println(cSrc);
        // 加密
        String enString = Encrypt(cSrc, cKey);
        System.out.println("加密后的字串是：" + enString);

        // 解密
        String DeString = Decrypt(enString, cKey);
        System.out.println("解密后的字串是：" + DeString);
    }
}
