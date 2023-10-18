package core.colin.basic.utils;

import static java.lang.Integer.parseInt;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: PL
 * Date: 2022/11/7
 * Desc:
 */
public class CommonUtils {

    private static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断字符串是否是整型
     *
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        try {
            if (!isEmpty(str)) {
                parseInt(str);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static int toIntZero(Integer i) {
        return i == null ? 0 : i;
    }

    public static int toInteger(String str) {
        try {
            if (!isEmpty(str)) {
                return parseInt(str);
            }
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 判断字符串是否是Long类型
     */
    public static boolean isLong(String str) {
        try {
            if (!isEmpty(str)) {
                Long.parseLong(str);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static long toLong(String str) {
        try {
            if (!isEmpty(str)) {
                return Long.parseLong(str);
            }
        } catch (Exception e) {
        }
        return 0L;
    }

    /**
     * 判断字符串是否是Float类型
     *
     * @param str str只能是非科学计数法。 科学计数法会字符会返回true，
     * @return
     */
    public static boolean isFloat(String str) {
        try {
            if (!isEmpty(str)) {
                Float.parseFloat(str);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * @param str
     * @return
     */
    public static float toFloat(String str) {
        try {
            if (!isEmpty(str)) {
                return Float.parseFloat(str);
            }
        } catch (Exception e) {
        }
        return 0f;
    }

    /**
     * 判断字符串是否是Double类型
     *
     * @param str str只能是非科学计数法。 科学计数法会字符会返回true，
     * @return
     */
    public static boolean isDouble(String str) {
        try {
            if (!isEmpty(str)) {
                Double.parseDouble(str);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }


    /**
     * @param str
     * @return
     */
    public static double toDouble(String str) {
        try {
            if (!isEmpty(str)) {
                return Double.parseDouble(str);
            }
        } catch (Exception e) {
        }
        return 0d;
    }

    /*
     * 是否为浮点数？double或float类型。
     * @param str 传入的字符串。
     * @return 是浮点数返回true,否则返回false。
     */
    public static boolean isDoubleOrFloat(String str) {
        if (isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static String null2TrimLength0(final String s) {
        return s == null ? "" : s.trim();
    }


    public static String null2Length0(final String s) {
        return s == null ? "" : s;
    }

    public static boolean isWhitespace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (Character.isWhitespace(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }


    /**
     * Description: Java8 Stream分割list集合，此函数的作用是，传入指定的List集合和指定的数量，输出结果是新集合，新集合中包含的在若干个子集合，每个子集合的长度是splitSize
     *
     * @param list      传入的list集合
     * @param splitSize 表示每splitSize个对象分割成一组
     * @return list集合分割后的集合
     */
    public static <T> List<List<T>> splitList(List<T> list, int splitSize) {
        //判断集合是否为空
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        //计算分割后的大小
        int maxSize = (list.size() + splitSize - 1) / splitSize;
        //开始分割
        return Stream.iterate(0, n -> n + 1)
                .limit(maxSize)
                .parallel()
                .map(a -> list.parallelStream()
                        .skip(a * splitSize)
                        .limit(splitSize)
                        .collect(Collectors.toList()))
                .filter(b -> !b.isEmpty())
                .collect(Collectors.toList());
    }

    public static String splitString(String text) {
        if (StringUtils.isTrimEmpty(text)) {
            return text;
        }
        String regEx = "[\n`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， ·、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(text);//这里把想要替换的字符串传进来
        String newString = m.replaceAll(regEx).trim();//将替换后的字符串存在变量newString中
        if (StringUtils.isTrimEmpty(newString) || newString.length() < 20) {
            return newString;
        }
        return newString.substring(0, 20);
    }

    public static String byte2FitMemorySize(final long byteNum) {
        if (byteNum < 0) {
            //shouldn't be less than zero!
            return null;
        } else if (byteNum < 1024) {
            return String.format(Locale.getDefault(), "%.1fB", (double) byteNum);
        } else if (byteNum < 1048576) {
            return String.format(Locale.getDefault(), "%.1fKB", (double) byteNum / 1024);
        } else if (byteNum < 1073741824) {
            return String.format(Locale.getDefault(), "%.1fMB", (double) byteNum / 1048576);
        } else {
            return String.format(Locale.getDefault(), "%.1fGB", (double) byteNum / 1073741824);
        }
    }

    /**
     * 格式化单位
     */
    public static String formatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return "0KB";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    public static String getExtensionByFilePath(String filePath) {
        String fe = "";
        int i = filePath.lastIndexOf('.');
        if (i > 0) {
            fe = filePath.substring(i + 1);
        }
        return fe;
    }

}
