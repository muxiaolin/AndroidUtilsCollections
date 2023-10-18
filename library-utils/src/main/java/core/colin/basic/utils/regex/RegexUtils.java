package core.colin.basic.utils.regex;

import com.blankj.utilcode.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串正则匹配
 */
public class RegexUtils {

    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final int PASSWORD_MAX_LENGTH = 20;

    private static final String REGEX_E_MAIL = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2," +
            "4}|[0-9]{1,3})(\\]?)$";
    private static final String REGEX_PHONE = "^(1[3456789][0-9])\\d{8}";
    private static final String REGEX_PASSWORD_NUN_AND_EN = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{" + PASSWORD_MIN_LENGTH + "," + PASSWORD_MAX_LENGTH + "}$";
    private static final String REGEX_PASSWORD_NUN_OR_EN = "^[a-z0-9A-Z]{" + PASSWORD_MIN_LENGTH + "," + PASSWORD_MAX_LENGTH + "}$";
    private static final String REGEX_ID_NUM = "^\\d{15}|\\d{18}|\\d{17}(\\d|X|x)";
    private static final String REGEX_ID_CARD = "(^\\d{15}$)|(^\\d{17}([0-9]|X)$)";
    private static final String REGEX_PASSWORD_LENGTH = "^.{" + PASSWORD_MIN_LENGTH + "," + PASSWORD_MAX_LENGTH + "}$";
    private static final String REGEX_PHONE_LENGTH = "^.{11}$";
    private static final String REGEX_PHONE_HIDE = "(\\d{3})\\d{4}(\\d{4})";
    private static final String REGEX_E_MAIL_HIDE = "(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)";
    private static final String REGEX_ID_CARD_HIDE = "(\\d{6})\\d*([0-9a-zA-Z]{4})";


    /**
     * 邮箱格式是否正确
     *
     * @param email String
     * @return boolean
     */
    public static boolean matchEmail(String email) {
        return match(email, REGEX_E_MAIL);
    }

    /**
     * 手机号格式是否正确
     *
     * @param phone String
     * @return boolean
     */
    public static boolean matchPhone(String phone) {
        return match(phone, REGEX_PHONE);
    }

    /**
     * 手机号长度是否正确
     *
     * @param phone String
     * @return boolean
     */
    public static boolean matchPhoneLength(String phone) {
        return match(phone, REGEX_PHONE_LENGTH);
    }

    /**
     * 密码格式是否正确
     *
     * @param psw String
     * @return boolean
     */
    public static boolean matchPassword(String psw) {
        return match(psw, REGEX_PASSWORD_NUN_AND_EN);
    }

    /**
     * 密码格式是否正确
     *
     * @param psw String
     * @return boolean
     */
    public static boolean matchPasswordNumOrEn(String psw) {
        return match(psw, REGEX_PASSWORD_NUN_OR_EN);
    }

    /**
     * 密码长度是否正确
     *
     * @param psw String
     * @return boolean
     */
    public static boolean matchPasswordLength(String psw) {
        return match(psw, REGEX_PASSWORD_LENGTH);
    }

    /**
     * 身份证号格式是否正确
     *
     * @param id String
     * @return boolean
     */
    public static boolean matchIdNum(String id) {
        return match(id, REGEX_ID_NUM);
    }

    //

    /**
     * 身份证号格式是否正确， 判断是否符合身份证号码的规范
     *
     * @param IDCard String
     * @return boolean
     */
    public static boolean matchIDCard(String IDCard) {
        return match(IDCard == null ? null : IDCard.toUpperCase(), REGEX_ID_CARD);

    }

    /**
     * 字符串正则匹配
     *
     * @param s     待匹配字符串
     * @param regex 正则表达式
     * @return boolean
     */
    public static boolean match(String s, String regex) {
        if (s == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }

    /**
     * 手机号用****号隐藏中间数字
     *
     * @param phone String
     * @return String
     */
    public static String hidePhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return null;
        }
        return phone.replaceAll(REGEX_PHONE_HIDE, "$1****$2");
    }

    /**
     * 邮箱用****号隐藏前面的字母
     *
     * @param email String
     * @return String
     */
    public static String hideEmail(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }
        return email.replaceAll(REGEX_E_MAIL_HIDE, "$1****$3$4");
    }

    /**
     * 隐藏身份证中间四位
     *
     * @param idCard
     * @return
     */
    public static String hideIdCard(String idCard) {
        if (idCard == null || idCard.isEmpty()) {
            return null;
        }
        return idCard.replaceAll(REGEX_ID_CARD_HIDE, "$1********$2");
    }

    /**
     * 隐藏姓名
     *
     * @param name
     * @return
     */
    public static String hideName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
//        return name.replaceAll("([\\d\\D]{1})(.*)", "$1**");
        String showName;
        char[] r = name.toCharArray();
        if (r.length == 1) {
            showName = name;
        } else if (r.length == 2) {
            showName = name.replaceFirst(name.substring(1), "*");
        } else {
            showName = name.replaceFirst(name.substring(1, r.length - 1), "*");
        }
        return showName;
    }


    /**
     * 星号隐藏字符串前面几个字符
     *
     * @param str        源字符串
     * @param lastLength 后面剩几个字符不处理
     * @return
     */
    public static String hideHlNo(String str, int firstLength, int lastLength) {
        if (StringUtils.isTrimEmpty(str) || str.length() < lastLength) {
            return "******";
        }
        if (firstLength >= lastLength || str.length() <= (firstLength + lastLength)) {
            char[] arr = str.toCharArray();
            for (int i = 0; i < arr.length - lastLength; i++) {
                arr[i] = '*';
            }
            return String.valueOf(arr);
        }
        char[] arr = str.toCharArray();
        for (int i = firstLength; i < arr.length - lastLength; i++) {
            arr[i] = '*';
        }
        return String.valueOf(arr);
    }

    /**
     * 星号隐藏字符串前面几个字符
     *
     * @param str        源字符串
     * @param lastLength 后面剩几个字符不处理
     * @return
     */
    public static String hidePreChar(String str, int lastLength) {
        if (StringUtils.isTrimEmpty(str) || str.length() < lastLength) {
            return str;
        }
        char[] arr = str.toCharArray();
        for (int i = 0; i < arr.length - lastLength; i++) {
            arr[i] = '*';
        }
        return String.valueOf(arr);
    }


}
