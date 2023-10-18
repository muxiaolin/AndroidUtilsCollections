package core.colin.basic.utils.codec;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * author : 彭林
 * date   : 2020/7/23
 * desc   :
 */
public class RSAUtils {
    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";
    private static final String RSA = "RSA";
    private static final String ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";
    public static final String MD5withRSA = "MD5withRSA";
    //RSA最大加密明文大小
    private static final int MAX_ENCRYPT_BLOCK = 117;
    //RSA最大解密密文大小
    private static final int MAX_DECRYPT_BLOCK = 128;


    /**
     * 生成密钥对(公钥和私钥)
     *
     * @return
     * @throws Exception
     */
    public static Map<String, String> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ECB_PKCS1_PADDING);
        // 初始化密钥对生成器，密钥大小
        keyPairGen.initialize(1024);

        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, String> keyMap = new HashMap<>(2);
        //得到公钥字符串
        keyMap.put(PUBLIC_KEY, Base64Utils.encodeToString(publicKey.getEncoded()));
        //得到私钥字符串
        keyMap.put(PRIVATE_KEY, Base64Utils.encodeToString(privateKey.getEncoded()));
        return keyMap;
    }


    /**
     * 用私钥对信息生成数字签名
     *
     * @param data       加密数据
     * @param privateKey 私钥
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64Utils.decodeToBytes(privateKey);// 解密由base64编码的私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes); //构造PKCS8EncodedKeySpec对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA); // KEY_ALGORITHM 指定的加密算法
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec); // 取私钥匙对象
        Signature signature = Signature.getInstance(MD5withRSA);// 用私钥对信息生成数字签名
        signature.initSign(priKey);
        signature.update(data);

        return Base64Utils.encodeToString(signature.sign());
    }

    /**
     * 校验数字签名
     *
     * @param data      加密数据
     * @param publicKey 公钥
     * @param sign      数字签名
     * @return
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
        byte[] keyBytes = Base64Utils.decodeToBytes(publicKey); // 解密由base64编码的公钥
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);  // 构造X509EncodedKeySpec对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);  // KEY_ALGORITHM 指定的加密算法
        PublicKey pubKey = keyFactory.generatePublic(keySpec);   // 取公钥对象

        Signature signature = Signature.getInstance(MD5withRSA);
        signature.initVerify(pubKey);
        signature.update(data);

        return signature.verify(Base64Utils.decodeToBytes(sign));
    }


    /**
     * 获得私钥
     *
     * @param privateKey 私钥(Base64编码)
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String privateKey) throws Exception {
        byte[] encodedKey = Base64Utils.decodeToBytes(privateKey);
        // 取得私钥
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        PrivateKey privateK = keyFactory.generatePrivate(keySpec);
        return privateK;
    }

    /**
     * 获得公钥
     *
     * @param publicKey (Base64编码)
     * @return
     */
    private static PublicKey getPublicKey(String publicKey) throws Exception {
        byte[] decodedKey = Base64Utils.decodeToBytes(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        PublicKey publicK = keyFactory.generatePublic(x509KeySpec);
        return publicK;
    }

    /**
     * 用私钥解密
     *
     * @param dataStr    已加密数据
     * @param privateKey 私钥(Base64编码)
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKeyToString(String dataStr, String privateKey) throws Exception {
        return new String(decryptByPrivateKey(dataStr, privateKey), StandardCharsets.UTF_8);
    }


    /**
     * 用私钥解密
     *
     * @param dataStr    已加密数据
     * @param privateKey 私钥(Base64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(String dataStr, String privateKey) throws Exception {
        return decryptByPrivateKey(Base64Utils.decodeToBytes(dataStr), privateKey);
    }

    /**
     * 用私钥解密
     *
     * @param data       已加密数据
     * @param privateKey 私钥(Base64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] data, String privateKey) throws Exception {
        // 取得私钥
        PrivateKey privateK = getPrivateKey(privateKey);
        // 对数据解密
        Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }


    /**
     * 用公钥解密
     *
     * @param data      已加密数据
     * @param publicKey 公钥(Base64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] data, String publicKey) throws Exception {
        // 取得公钥
        Key publicK = getPublicKey(publicKey);
        Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 用公钥加密
     *
     * @param dataStr
     * @param publicKey 公钥(Base64编码)
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKeyToString(String dataStr, String publicKey) throws Exception {
        return Base64Utils.encodeToString(encryptByPublicKey(dataStr, publicKey));
    }


    /**
     * 用公钥加密
     *
     * @param dataStr
     * @param publicKey 公钥(Base64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(String dataStr, String publicKey) throws Exception {
        return encryptByPublicKey(dataStr.getBytes(StandardCharsets.UTF_8), publicKey);
    }

    /**
     * 用公钥加密
     *
     * @param data
     * @param publicKey 公钥(Base64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
        // 取得公钥
        PublicKey publicK = getPublicKey(publicKey);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * 用私钥加密
     *
     * @param data
     * @param privateKey 私钥(Base64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {
        // 取得私钥
        PrivateKey privateK = getPrivateKey(privateKey);
        Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }


    public static void main(String[] args) {
//        try {
//            //生成密钥对
//            System.out.println(genKeyPair());
//        //加密字符串
//        String message = "hello, world!";
//        System.out.println("公钥为:" + keyMap.get(0));
//        System.out.println("私钥为:" + keyMap.get(1));
//        System.out.println("加密前的字符串为:" + message);
//        String messageEncryption = encrypt(message, keyMap.get(0));
//        System.out.println("加密后的字符串为:" + messageEncryption);
//        String messageDecrypt = decrypt(messageEncryption, keyMap.get(1));
//        System.out.println("解密后的字符串为:" + messageDecrypt);
//        String data = "{\"appParam\":\"{\\\"appVersion\\\":\\\"1.0.5\\\",\\\"channel\\\":\\\"app_cadre\\\",\\\"subChannel\\\":\\\"\\\",
//        \\\"appVersionCode\\\":\\\"20\\\"}\",\"channel\":\"app_cadre\",\"deviceParam\":\"{\\\"product\\\":\\\"PD2057\\\",
//        \\\"screenSize\\\":\\\"1080*2342\\\",\\\"imei\\\":\\\"a4edcc407ea5d22c\\\",\\\"deviceModel\\\":\\\"V2057A\\\",\\\"deviceName\\\":\\\"vivo\\\",
//        \\\"uuid\\\":\\\"a4edcc407ea5d22c\\\"}\",\"event\":\"app_launch\",\"eventParam\":null,\"identityId\":\"bfaedf94-54e7-4e66-aa10-75fc1a56376d\",
//        \"loginFlag\":true,\"nonce\":\"8795wwxh5h07lbizqlbgrc3y6iq8xsra\",\"osParam\":\"{\\\"osVersionCode\\\":\\\"29\\\",\\\"osVersion\\\":\\\"10\\\",
//        \\\"osName\\\":\\\"android\\\"}\",\"userIp\":null}";
//        String sign = encrypt(data);
//        System.out.println(sign);
//        System.out.println(decrypt("XsB3qXZXz9nUT1vJiqiAoqLEq/vtXrByaR2kE2BLdMRfNG4zrWVOkmn0FO8GfvakJ9FQMDlPhvZ/5iX9/8VlJzjOtRVhF3kC
//        +rDnZ9Fa0BGTBwCmcEXDHm0iSrZXQWLQSjPJeXtFEwKaRqY5whEhruHs+MUTwn3WaZTel4ZMDocQVXkibwHVv/QB73gGn265Ex0W5hkm2IvCDeX4NAh+R0ogW63XRn3+9hY+CUoIrPiyQqU2
//        /OW7BMqqK+ZXqeLFQV78+9V5rw4KLx5rTBTjAO3UdSU68/p0XgQG9hfbwPcA+EPn406eiLHQA4YbNKBYikhO9dffl2hvgaiEZXgok1iM8KDN1900vIdb8Tn
//        +f1y2cpJmew5aL5mkx3ZJamDYlnqlWIudENqy7+In/N6BrOgpBuIP5gIv6r6BwXh/t/M0JrD7inko1Sj7HHtA7CLkdtEPO2dVk8xbNH3ZM4W7aVVgwsbsKguft0d
//        /GgRvcO0KmC6NUy46c8wHqfaF7+16K5NpRTb4oaeK0tZ4FE5YbKooVj/aL3TQigC1+Hau+Sc9bBSUjktn55VL6M0Hnwt7JbS6sT0DEFK7
//        /8Spc0coXnzxA4IFyax8BddvfES19dAUSLEaCVSG8dcJjogaPvgJlLKjMGOdiFh/6QPCZpvxK1D0HY+vgpRe33J1xzhcXZFX5Q3S+5ZRyLRj9cuIW1GmzKu/6jutgtXFV4tVUVWM
//        +JhbcfeGLNwRcFyLl3fzZuL6XBhoZIJRixs2/d8W1iByVYHO8kA790+JODKy7lgRf8GkB0uvg8DUF6jPSkVKofN0OjhWyodeA/asX/POnrjAZ
//        +Dncv5WJ7b6n69VSGEcKjX4ezeVKTZG0uc8vdBKQaHMNnSwoIlheeCSI1js+mTUoKWqjerXVZKhrTnAjSialoPMAF+pj6fOREEH/V7gckk="));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
