package core.colin.basic.utils.codec;

/**
 * author : 彭林
 * date   : 2020/7/23
 * desc   :
 */
public enum Algorithm {

    SHA("SHA"),
    MD5("MD5"),
    Hmac_MD5("HmacMD5"),
    Hmac_SHA1("HmacSHA1"),
    Hmac_SHA256("HmacSHA256"),
    Hmac_SHA384("HmacSHA384"),
    Hmac_SHA512("HmacSHA512"),
    DES("DES"),
    RSA("RSA");

    private String type;

    Algorithm(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
