package core.colin.basic.utils.codec;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * author : 彭林
 * date   : 2020/7/23
 * desc   :
 */
public class MD5Utils {

    private static final String MD5 = "MD5";

    @NonNull
    public static String encode(String string, String slat) {
        return encode(string + slat);
    }

    /**
     * @param string
     * @param times  加密次数
     * @return
     */
    @NonNull
    public static String encode(String string, int times) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        if (times <= 1) {
            return encode(string);
        }
        String md5 = encode(string);
        for (int i = 0; i < times - 1; i++) {
            md5 = encode(md5);
        }
        return md5;
    }


    /**
     * @param str 要加密的字符串
     * @return String 加密的字符串
     */
    public static String encode(String str) {
        return encode(str.getBytes());
    }

    /**
     * 十六进制
     *
     * @param buffer
     * @return
     */
    public static String encode(byte[] buffer) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance(MD5);
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param buffer
     * @return
     */
    public static byte[] getRawDigest(byte[] buffer) {
        try {
            MessageDigest mdTemp = MessageDigest.getInstance(MD5);
            mdTemp.update(buffer);
            return mdTemp.digest();

        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 对文件进行md5
     *
     * @param filePath 文件路径
     * @return
     */
    public static String encodeFile(final String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return null;
        }
        return encodeFile(new File(filePath));
    }

    /**
     * 文件md5
     *
     * @param file
     * @return
     */
    public static String encodeFile(final File file) {
        return encodeFile(file, 1024 * 100);
    }


    public static String encodeFile(final File file, final int bufLen) {
        if (file == null || bufLen <= 0 || !file.exists()) {
            return null;
        }

        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            String md5 = getMD5(fin, (int) (bufLen <= file.length() ? bufLen : file.length()));
            fin.close();
            return md5;

        } catch (Exception e) {
            return null;

        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException e) {

            }
        }
    }

    private static String getMD5(final InputStream is, final int bufLen) {
        if (is == null || bufLen <= 0) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance(MD5);
            StringBuilder md5Str = new StringBuilder(32);

            byte[] buf = new byte[bufLen];
            int readCount = 0;
            while ((readCount = is.read(buf)) != -1) {
                md.update(buf, 0, readCount);
            }

            byte[] hashValue = md.digest();

            for (int i = 0; i < hashValue.length; i++) {
                md5Str.append(Integer.toString((hashValue[i] & 0xff) + 0x100, 16).substring(1));
            }
            return md5Str.toString();
        } catch (Exception e) {
            return null;
        }
    }

}
