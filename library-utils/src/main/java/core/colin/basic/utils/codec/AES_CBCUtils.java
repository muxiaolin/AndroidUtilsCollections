//package com.ke.basic.utils.codec;
//
///**
// * AES加密解密（CBC模式）
// *
// * @author PengLin
// * @date 2020/1/15
// * @desc
// */
//
//import org.bouncycastle.jce.provider.BouncyCastleProvider;
//
//import java.security.Key;
//import java.security.Security;
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.IvParameterSpec;
//import javax.crypto.spec.SecretKeySpec;
//
//public class AES_CBCUtils {
//    private byte[] Liz_KEY;
//    private byte[] Liz_IV;
//
//    private final static String KEY_ALGORITHM = "AES";  // 加密方式
//    private final static String ALGORITHMSTR = "AES/CBC/PKCS7Padding";  // 数据填充方式
//    private boolean initialized = false;
//
//    public AES_CBCUtils() {
//    }
//
//    public AES_CBCUtils(String key, String iv) {
//        Liz_KEY = key.getBytes();
//        Liz_IV = iv.getBytes();
//    }
//
//    // BouncyCastle作为安全提供,防止我们加密解密时候因为jdk内置的不支持改模式运行报错
//    private void initialize() {
//        if (initialized) {
//            return;
//        }
//        Security.addProvider(new BouncyCastleProvider());
//        initialized = true;
//    }
//
//    // 加密
//    public byte[] encrypt(byte[] originalContent) {
//        initialize();
//        try {
//            Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
//            SecretKeySpec skeySpec = new SecretKeySpec(Liz_KEY, KEY_ALGORITHM);
//            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(Liz_IV));
//            return cipher.doFinal(originalContent);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    // 解密
//    public byte[] decrypt(byte[] content) {
//        initialize();
//        try {
//            Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
//            Key sKeySpec = new SecretKeySpec(Liz_KEY, KEY_ALGORITHM);
//            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, new IvParameterSpec(Liz_IV));// 初始化
//            return cipher.doFinal(content);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//    //加密后，Base64编码处理，并去除base64的换行符
//    public String encodeSign(String jsonContent) {
//        byte[] encyptData = encrypt(jsonContent.getBytes());
//        byte[] base64Data = org.bouncycastle.util.encoders.Base64.encode(encyptData);
////        byte[] base64Data = Base64.encode(encyptData, Base64.DEFAULT);
//
//        return new String(base64Data).replaceAll("[\\s*]", "");
//    }
//
//
//    // 收到数据后应先反编码base64得到原始加密值，再进行解密
//    public String decodeSign(String data) throws Exception {
//        byte[] base64Data = org.bouncycastle.util.encoders.Base64.decode(data);
////        byte[] base64Data = Base64.decode(data, Base64.DEFAULT);
//
//        byte[] decryptData = decrypt(base64Data);
//        return new String(decryptData, "GBK");
//    }
//
//
//}
