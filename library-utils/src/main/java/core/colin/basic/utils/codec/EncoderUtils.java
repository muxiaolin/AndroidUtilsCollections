package core.colin.basic.utils.codec;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author PengLin
 * @desc
 * @date 2018/5/21
 */
public final class EncoderUtils {

    private EncoderUtils() {
    }

    public static String getString(byte[] data, String charset) {
        return getString(data, 0, data.length, charset);
    }


    public static String getString(byte[] data, int offset, int length, String charset) {
        try {
            return new String(data, offset, length, charset);
        } catch (UnsupportedEncodingException e) {
            return new String(data, offset, length);
        }
    }

    public static byte[] getBytes(String data, String charset) {
        try {
            return data.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            return data.getBytes();
        }
    }

    public static byte[] getAsciiBytes(String data) {
        try {
            return data.getBytes(StandardCharsets.US_ASCII.name());
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String getAsciiString(byte[] data) {
        return getAsciiString(data, 0, data.length);
    }

    public static String getAsciiString(byte[] data, int offset, int length) {
        try {
            return new String(data, offset, length, StandardCharsets.US_ASCII.name());
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }


    public static String URLEncoder(String encodeContent) {
        if (TextUtils.isEmpty(encodeContent)) {
            return encodeContent;
        }
        try {
            return URLEncoder.encode(encodeContent, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return encodeContent;
        }

    }


    public static String strToSha(String data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(data.getBytes());
        StringBuffer buf = new StringBuffer();
        byte[] bits = md.digest();
        for (byte a : bits) {
            int a2 = a;
            if (a < 0) {
                a2 = a + 256;
            }
            if (a2 < 16) {
                buf.append("0");
            }
            buf.append(Integer.toHexString(a2));
        }
        return buf.toString();
    }


}