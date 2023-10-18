package core.colin.basic.utils.codec;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

/**
 * author : 彭林
 * date   : 2020/7/23
 * desc   :
 */
public class Base64Utils {

    /**
     * Base64加密
     *
     * @param bytes
     */
    public static byte[] encodeToBytes(byte[] bytes) {
        return Base64.encode(bytes, Base64.DEFAULT);
    }

    /**
     * Base64加密
     *
     * @param text
     */
    public static byte[] encodeToBytes(String text) {
        return encodeToBytes(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64加密
     *
     * @param bytes
     */
    public static String encodeToString(byte[] bytes) {
        byte[] encodeBytes = encodeToBytes(bytes);
        return new String(encodeBytes, StandardCharsets.UTF_8);
    }

    /**
     * Base64加密
     *
     * @param text
     */
    public static String encodeToString(String text) {
        return encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64解密
     *
     * @param bytes
     */
    public static byte[] decodeToBytes(byte[] bytes) {
        return Base64.decode(bytes, Base64.DEFAULT);
    }

    /**
     * Base64解密
     *
     * @param text
     */
    public static byte[] decodeToBytes(String text) {
        return decodeToBytes(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64解密
     *
     * @param bytes
     */
    public static String decodeToString(byte[] bytes) {
        final byte[] decodeBytes = decodeToBytes(bytes);
        return new String(decodeBytes, StandardCharsets.UTF_8);
    }

    /**
     * Base64解密
     *
     * @param text
     */
    public static String decodeToString(String text) {
        final byte[] decodeBytes = decodeToBytes(text);
        return new String(decodeBytes, StandardCharsets.UTF_8);
    }

}
