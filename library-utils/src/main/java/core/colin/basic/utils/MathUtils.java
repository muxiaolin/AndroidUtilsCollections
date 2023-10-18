/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package core.colin.basic.utils;

import androidx.annotation.Size;

import com.blankj.utilcode.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MathUtils {
    //默认除法运算精度
    private static final int DEF_DIV_SCALE = 10;

    public static String formatBooleanChineseChar(Boolean str) {
        return formatBooleanChineseChar(str == null ? null : Boolean.toString(str));
    }

    public static String formatBooleanChineseChar(String str) {
        if (Boolean.TRUE.toString().equals(str)) {
            return "是";
        } else if (Boolean.FALSE.toString().equals(str)) {
            return "否";
        }
        return null;
    }

    /**
     * 动态计算金额单位
     *
     * @param value 单位元
     * @return 数组[value, unit]
     */
    @Size(2)
    public static String[] dynamicMoneyUnit(String value, int scale) {
        String[] unitArray = new String[2];
        Double money = !CommonUtils.isDouble(value) ? null : Double.parseDouble(value);
        if (money == null) {
            unitArray[0] = "";
            unitArray[1] = "元";
        } else if (money >= 100000000) { //1亿以上
            BigDecimal b1 = new BigDecimal(Double.toString(money));
            BigDecimal b2 = new BigDecimal(Double.toString(100000000));
            unitArray[0] = b1.divide(b2, scale, RoundingMode.HALF_UP).toString();
            unitArray[1] = "亿元";
        } else if (money >= 10000) { //1万以上
            BigDecimal b1 = new BigDecimal(Double.toString(money));
            BigDecimal b2 = new BigDecimal(Double.toString(10000));
            unitArray[0] = b1.divide(b2, scale, RoundingMode.HALF_UP).toString();
            unitArray[1] = "万元";
        } else {
            unitArray[0] = value;
            unitArray[1] = "元";
        }
        return unitArray;
    }

    /**
     * @param value 单位：元
     * @return 单位：万元
     */
    public static String yuanToThousand(String value) {
        if (!CommonUtils.isDouble(value)) {
            return null;
        }
        BigDecimal b1 = new BigDecimal(value);
        BigDecimal b2 = new BigDecimal(Double.toString(10000));
        return stripTrailingZeros(b1.divide(b2, 6, RoundingMode.HALF_UP).toString());
    }

    /**
     * @param value 万元值
     * @return 单位元
     */
    public static String thousandToYuan(String value) {
        if (!CommonUtils.isDouble(value)) {
            return null;
        }
        BigDecimal b = new BigDecimal(value);
        return b.multiply(new BigDecimal(10000)).toString();
    }

    /**
     * @param value 百分化数值 (0~1)
     * @return 百分化数值 (0~100)
     */
    public static String toPercentValue(String value) {
        if (!CommonUtils.isDouble(value)) {
            return "0";
        }
        BigDecimal b1 = new BigDecimal(value);
        BigDecimal b2 = new BigDecimal(Float.toString(100));
        float percent = b1.multiply(b2).floatValue();
        return stripTrailingZeros(percent);
    }

    /**
     * 格式化百分比
     *
     * @param percent 百分化数值 (0~1)
     * @return 带%号的百分数
     */
    public static String formatPercent(String percent) {
        if (!CommonUtils.isDouble(percent)) {
            return "0%";
        }
        return formatPercent(Float.parseFloat(percent));
    }

    /**
     * 格式化百分比
     *
     * @param percent 百分化数值 (0~1)
     * @return 带%号的百分数
     */
    public static String formatPercent(float percent) {
        //传入格式模板 带%会自动乘以100
        DecimalFormat df = new DecimalFormat("##.###%");
        return df.format(percent);
    }

    /**
     * 格式化百分比
     *
     * @param num1 分子
     * @param num2 分母
     * @return
     */
    public static String formatPercent(float num1, float num2) {
        if (num2 == 0) {
            return "0%";
        }
        //传入格式模板 带%会自动乘以100
        DecimalFormat df = new DecimalFormat("##.###%");
        return df.format((float) num1 / (float) num2);
    }

    /**
     * 去掉数值末位0, 非数值时返回占位符"--"
     *
     * @param value
     * @return
     */
    public static String stripTrailingZerosDefault(String value) {
        return stripTrailingZeros(value, "--");
    }


    /**
     * 去掉数值末位0, 非数值时返回0
     *
     * @param value
     * @return
     */
    public static String stripTrailingZeros(String value) {
        return stripTrailingZeros(value, "0");
    }

    /**
     * 去掉数值末位0, 非数值时返回占位符
     *
     * @param value
     * @return
     */
    public static String stripTrailingZeros(String value, String placeholder) {
        if (!CommonUtils.isDouble(value)) {
            return placeholder;
        }
        return new BigDecimal(value).stripTrailingZeros().toPlainString();
    }

    /**
     * 去掉数值末位0
     *
     * @param value
     * @return
     */
    public static String stripTrailingZeros(double value) {
        if (value == 0) {
            return "0";
        }
        return new BigDecimal(Double.toString(value)).stripTrailingZeros().toPlainString();
    }

    /**
     * 处理空值，为空值时返回默认占位符“--”
     *
     * @param value
     * @return
     */
    public static String formatCharPlaceholder(String value) {
        return formatCharPlaceholder(value, "--");
    }

    /**
     * 处理空值，为空值时返回占位符
     *
     * @param value
     * @return
     */
    public static String formatCharPlaceholder(String value, String placeholder) {
        return StringUtils.isTrimEmpty(value) ? placeholder : value;
    }

    /**
     * 格式化金额, 为空值时返回默认占位符“--”
     *
     * @param value
     * @param separator 整数部分是否使用分位符
     * @return
     */
    public static String formatNumPlaceholder(String value, boolean separator) {
        return formatNumPlaceholder(value, separator, "--");
    }

    /**
     * 格式化金额, 为空值时返回默认占位符“--”
     *
     * @param value
     * @param separator 整数部分是否使用分位符
     * @return
     */
    public static String formatNumPlaceholder(String value, boolean separator, int len) {
        return formatNumPlaceholder(value, separator, "--", len);
    }

    /**
     * 格式化金额
     *
     * @param value
     * @param separator   整数部分是否使用分位符
     * @param placeholder 占位符
     * @return
     */
    public static String formatNumPlaceholder(String value, boolean separator, String placeholder) {
        return formatNumPlaceholder(value, separator, placeholder, 2);
    }

    /**
     * 格式化金额
     *
     * @param value
     * @param separator   整数部分是否使用分位符
     * @param placeholder 占位符
     * @return
     */
    public static String formatNumPlaceholder(String value, boolean separator, String placeholder, int len) {
        if (StringUtils.isTrimEmpty(value)) {
            return placeholder;
        }
        return formatNum(value, separator, len, false);
    }


    /**
     * 格式化金额，整数部分使用分位符, 小数点后保留2位, 小数位不补零
     *
     * @param num
     * @return
     */
    public static String formatNum(String num) {
        return formatNum(num, true, 2, false);
    }


    /**
     * 格式化金额，默认整数部分使用分位符, 小数点后保留2位, 小数位不补零
     *
     * @param num
     * @return
     */
    public static String formatNum(double num) {
        return formatNum(num, true, 2, false);
    }

    /**
     * 格式化金额，默认小数点后保留2位, 小数位不补零
     *
     * @param num
     * @param separator 整数部分是否使用分位符
     * @return
     */
    public static String formatNum(String num, boolean separator) {
        return formatNum(num, separator, 2, false);
    }

    /**
     * 格式化金额，默认小数点后保留2位, 小数位不补零
     *
     * @param num
     * @param separator 整数部分是否使用分位符
     * @return
     */
    public static String formatNum(double num, boolean separator) {
        return formatNum(num, separator, 2, false);
    }

    /**
     * 格式化金额，默认小数位不补零
     *
     * @param num
     * @param separator 整数部分是否使用分位符
     * @param len       小数点后保留几位
     * @return
     */
    public static String formatNum(String num, boolean separator, int len) {
        return formatNum(num, separator, len, false);
    }

    /**
     * 格式化金额，默认小数位不补零
     *
     * @param num
     * @param separator 整数部分是否使用分位符
     * @param len       小数点后保留几位
     * @return
     */
    public static String formatNum(double num, boolean separator, int len) {
        return formatNum(num, separator, len, false);
    }


    /**
     * 格式化金额
     *
     * @param num        数字
     * @param separator  整数部分是否使用分位符
     * @param len        小数点后保留几位
     * @param zeroPadded 小数位是否补零
     * @return
     */
    public static String formatNum(String num, boolean separator, int len, boolean zeroPadded) {
        if (!CommonUtils.isDouble(num)) {
            return num;
        }
        return formatNum(Double.parseDouble(num), separator, len, zeroPadded);
    }

    /**
     * 格式化金额
     *
     * @param num
     * @param separator  整数部分是否使用分位符
     * @param len        小数点后保留几位
     * @param zeroPadded 小数位是否补零
     * @return
     */
    public static String formatNum(double num, boolean separator, int len, boolean zeroPadded) {
        final StringBuilder buff = new StringBuilder(separator ? "#,###,##0" : "##0");
        if (len > 0) {
            buff.append(".");
            for (int i = 0; i < len; i++) {
                buff.append(zeroPadded ? "0" : "#");
            }
        }
        BigDecimal b = new BigDecimal(Double.toString(num));
        BigDecimal one = new BigDecimal(Double.toString(1));
        double value = b.divide(one, len, RoundingMode.HALF_UP).doubleValue();
        final NumberFormat formater = new DecimalFormat(buff.toString());
        return formater.format(value);
    }


    //保留一位小数  不四舍五入
    public static String roundingMode(float value) {
        DecimalFormat formater = new DecimalFormat();
        formater.setMaximumFractionDigits(2);
        formater.setGroupingSize(0);
        formater.setRoundingMode(RoundingMode.FLOOR);
        return formater.format(value);
    }

    //保留一位小数
    public static String roundingMoreMode(double value) {
        DecimalFormat myformat = new DecimalFormat("0.0");
        return myformat.format(value);
    }

    /**
     * 提供精确的加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */

    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */

    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后10位，以后的数字四舍五入。
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */

    public static double div(double v1, double v2) {
        return div(v1, v2, DEF_DIV_SCALE);
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供精确的小数位四舍五入处理。
     *
     * @param v     需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 四舍五入处理。
     *
     * @param v     原数
     * @param scale 保留几位小数
     * @return 四舍五入后的值
     */
    public static BigDecimal roundDecimal(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        return new BigDecimal(v).setScale(scale, BigDecimal.ROUND_HALF_UP);
    }


    /**
     * 提供精确的小数位四舍五入处理。
     *
     * @param v     需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static float roundFloat(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal bd = new BigDecimal(Double.toString(v));
        return bd.setScale(scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static int roundInt(double v) {
        BigDecimal bd = new BigDecimal(v);
        return bd.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
    }

    public static int clamp(int value, int min, int max) {
        return value < min ? min : value > max ? max : value;
    }

    public static float clamp(float value, float min, float max) {
        return value < min ? min : value > max ? max : value;
    }

    public static int lerp(int start, int end, float fraction) {
        return (int) (start + (end - start) * fraction);
    }

    public static float lerp(float start, float end, float fraction) {
        return start + (end - start) * fraction;
    }

    public static float unlerp(int start, int end, int value) {
        int domainSize = end - start;
        if (domainSize == 0) {
            throw new IllegalArgumentException("Can't reverse interpolate with domain size of 0");
        }
        return (float) (value - start) / domainSize;
    }

    public static float unlerp(float start, float end, float value) {
        float domainSize = end - start;
        if (domainSize == 0) {
            throw new IllegalArgumentException("Can't reverse interpolate with domain size of 0");
        }
        return (value - start) / domainSize;
    }

    /**
     * 描述：字节数组转换成16进制串.
     *
     * @param b      the b
     * @param length the length
     * @return the string
     */
    public static String byte2HexStr(byte[] b, int length) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < length; ++n) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else {
                hs = hs + stmp;
            }
            hs = hs + ",";
        }
        return hs.toUpperCase();
    }

    /**
     * 二进制转为十六进制.
     *
     * @param binary the binary
     * @return char hex
     */
    public static char binaryToHex(int binary) {
        char ch = ' ';
        switch (binary) {
            case 0:
                ch = '0';
                break;
            case 1:
                ch = '1';
                break;
            case 2:
                ch = '2';
                break;
            case 3:
                ch = '3';
                break;
            case 4:
                ch = '4';
                break;
            case 5:
                ch = '5';
                break;
            case 6:
                ch = '6';
                break;
            case 7:
                ch = '7';
                break;
            case 8:
                ch = '8';
                break;
            case 9:
                ch = '9';
                break;
            case 10:
                ch = 'a';
                break;
            case 11:
                ch = 'b';
                break;
            case 12:
                ch = 'c';
                break;
            case 13:
                ch = 'd';
                break;
            case 14:
                ch = 'e';
                break;
            case 15:
                ch = 'f';
                break;
            default:
                ch = ' ';
        }
        return ch;
    }


    /**
     * 一维数组转为二维数组
     *
     * @param m      the m
     * @param width  the width
     * @param height the height
     * @return the int[][]
     */
    public static int[][] arrayToMatrix(int[] m, int width, int height) {
        int[][] result = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int p = j * height + i;
                result[i][j] = m[p];
            }
        }
        return result;
    }


    /**
     * 二维数组转为一维数组
     *
     * @param m the m
     * @return the double[]
     */
    public static double[] matrixToArray(double[][] m) {
        int p = m.length * m[0].length;
        double[] result = new double[p];
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                int q = j * m.length + i;
                result[q] = m[i][j];
            }
        }
        return result;
    }

    /**
     * 描述：int数组转换为double数组.
     *
     * @param input the input
     * @return the double[]
     */
    public static double[] intToDoubleArray(int[] input) {
        int length = input.length;
        double[] output = new double[length];
        for (int i = 0; i < length; i++) {
            output[i] = Double.parseDouble(String.valueOf(input[i]));
        }
        return output;
    }

    /**
     * 描述：int二维数组转换为double二维数组.
     *
     * @param input the input
     * @return the double[][]
     */
    public static double[][] intToDoubleMatrix(int[][] input) {
        int height = input.length;
        int width = input[0].length;
        double[][] output = new double[height][width];
        for (int i = 0; i < height; i++) {
            // 列
            for (int j = 0; j < width; j++) {
                // 行
                output[i][j] = Double.parseDouble(String.valueOf(input[i][j]));
            }
        }
        return output;
    }

    /**
     * 计算数组的平均值.
     *
     * @param pixels 数组
     * @return int 平均值
     */
    public static int average(int[] pixels) {
        float m = 0;
        for (int pixel : pixels) {
            m += pixel;
        }
        m = m / pixels.length;
        return (int) m;
    }

    /**
     * 计算数组的平均值.
     *
     * @param pixels 数组
     * @return int 平均值
     */
    public static int average(double[] pixels) {
        float m = 0;
        for (int i = 0; i < pixels.length; ++i) {
            m += pixels[i];
        }
        m = m / pixels.length;
        return (int) m;
    }

    /**
     * 描述：点在直线上.
     * 点A（x，y）,B(x1,y1),C(x2,y2) 点A在直线BC上吗?
     *
     * @param x
     * @param y
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public boolean pointAtSLine(double x, double y, double x1, double y1, double x2, double y2) {
        double result = (x - x1) * (y2 - y1) - (y - y1) * (x2 - x1);
        if (result == 0) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 描述：点在线段上.
     * 点A（x，y）,B(x1,y1),C(x2,y2)   点A在线段BC上吗?
     *
     * @param x
     * @param y
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static boolean pointAtELine(double x, double y, double x1, double y1, double x2, double y2) {
        double result = (x - x1) * (y2 - y1) - (y - y1) * (x2 - x1);
        if (result == 0) {
            if (x >= Math.min(x1, x2) && x <= Math.max(x1, x2)
                    && y >= Math.min(y1, y2) && y <= Math.max(y1, y2)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 描述：两条直线相交.
     * 点A（x1，y1）,B(x2,y2),C(x3,y3),SHB(x4,y4)   直线AB与直线CD相交吗?
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @param x4
     * @param y4
     * @return
     */
    public static boolean LineAtLine(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        double k1 = (y2 - y1) / (x2 - x1);
        double k2 = (y4 - y3) / (x4 - x3);
        if (k1 == k2) {
            //System.out.println("平行线");
            return false;
        } else {
            double x = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x3 * y4 - y3 * x4) * (x1 - x2)) / ((y2 - y1) * (x3 - x4) - (y4 - y3) * (x1 - x2));
            double y = (x1 * y2 - y1 * x2 - x * (y2 - y1)) / (x1 - x2);
            //System.out.println("直线的交点("+x+","+y+")");
            return true;
        }
    }

    /**
     * 描述：线段与线段相交.
     * 点A（x1，y1）,B(x2,y2),C(x3,y3),SHB(x4,y4)
     * 线段AB与线段CD相交吗?
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @param x4
     * @param y4
     * @return
     */
    public static boolean eLineAtELine(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        double k1 = (y2 - y1) / (x2 - x1);
        double k2 = (y4 - y3) / (x4 - x3);
        if (k1 == k2) {
            //System.out.println("平行线");
            return false;
        } else {
            double x = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x3 * y4 - y3 * x4) * (x1 - x2)) / ((y2 - y1) * (x3 - x4) - (y4 - y3) * (x1 - x2));
            double y = (x1 * y2 - y1 * x2 - x * (y2 - y1)) / (x1 - x2);
            //System.out.println("直线的交点("+x+","+y+")");
            if (x >= Math.min(x1, x2) && x <= Math.max(x1, x2)
                    && y >= Math.min(y1, y2) && y <= Math.max(y1, y2)
                    && x >= Math.min(x3, x4) && x <= Math.max(x3, x4)
                    && y >= Math.min(y3, y4) && y <= Math.max(y3, y4)) {
                //System.out.println("交点（"+x+","+y+"）在线段上");
                return true;
            } else {
                //System.out.println("交点（"+x+","+y+"）不在线段上");
                return false;
            }
        }
    }

    /**
     * 描述：线段直线相交.
     * 点A（x1，y1）,B(x2,y2),C(x3,y3),SHB(x4,y4)
     * 线段AB与直线CD相交吗?
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @param x4
     * @param y4
     * @return
     */
    public static boolean eLineAtLine(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        double k1 = (y2 - y1) / (x2 - x1);
        double k2 = (y4 - y3) / (x4 - x3);
        if (k1 == k2) {
            //System.out.println("平行线");
            return false;
        } else {
            double x = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x3 * y4 - y3 * x4) * (x1 - x2)) / ((y2 - y1) * (x3 - x4) - (y4 - y3) * (x1 - x2));
            double y = (x1 * y2 - y1 * x2 - x * (y2 - y1)) / (x1 - x2);
            //System.out.println("交点("+x+","+y+")");
            if (x >= Math.min(x1, x2) && x <= Math.max(x1, x2)
                    && y >= Math.min(y1, y2) && y <= Math.max(y1, y2)) {
                //System.out.println("交点（"+x+","+y+"）在线段上");
                return true;
            } else {
                //System.out.println("交点（"+x+","+y+"）不在线段上");
                return false;
            }
        }
    }

    /**
     * 描述：点在矩形内.
     * 矩形的边都是与坐标系平行或垂直的。
     * 只要判断该点的横坐标和纵坐标是否夹在矩形的左右边和上下边之间。
     * 点A（x，y）,B(x1,y1),C(x2,y2)   点A在以直线BC为对角线的矩形中吗?
     *
     * @param x
     * @param y
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static boolean pointAtRect(double x, double y, double x1, double y1, double x2, double y2) {
        if (x >= Math.min(x1, x2) && x <= Math.max(x1, x2) && y >= Math.min(y1, y2) && y <= Math.max(y1, y2)) {
            //System.out.println("点（"+x+","+y+"）在矩形内上");
            return true;
        } else {
            //System.out.println("点（"+x+","+y+"）不在矩形内上");
            return false;
        }
    }

    /**
     * 描述：矩形在矩形内.
     * 只要对角线的两点都在另一个矩形中就可以了.
     * 点A(x1,y1),B(x2,y2)，C(x1,y1),SHB(x2,y2) 以直线AB为对角线的矩形在以直线BC为对角线的矩形中吗?
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @param x4
     * @param y4
     * @return
     */
    public static boolean rectAtRect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        if (x1 >= Math.min(x3, x4) && x1 <= Math.max(x3, x4)
                && y1 >= Math.min(y3, y4) && y1 <= Math.max(y3, y4)
                && x2 >= Math.min(x3, x4) && x2 <= Math.max(x3, x4)
                && y2 >= Math.min(y3, y4) && y2 <= Math.max(y3, y4)) {
            //System.out.println("矩形在矩形内");
            return true;
        } else {
            //System.out.println("矩形不在矩形内");
            return false;
        }
    }

    /**
     * 描述：圆心在矩形内 .
     * 圆心在矩形中且圆的半径小于等于圆心到矩形四边的距离的最小值。
     * 圆心(x,y) 半径r  矩形对角点A（x1，y1），B(x2，y2)
     *
     * @param x
     * @param y
     * @param r
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static boolean circleAtRect(double x, double y, double r, double x1, double y1, double x2, double y2) {
        //圆心在矩形内
        if (x >= Math.min(x1, x2) && x <= Math.max(x1, x2)
                && y >= Math.min(y1, y2) && y <= Math.max(y1, y2)) {
            //圆心到4条边的距离
            double l1 = Math.abs(x - x1);
            double l2 = Math.abs(y - y2);
            double l3 = Math.abs(x - x2);
            double l4 = Math.abs(y - y2);
            if (r <= l1 && r <= l2 && r <= l3 && r <= l4) {
                //System.out.println("圆在矩形内");
                return true;
            } else {
                //System.out.println("圆不在矩形内");
                return false;
            }

        } else {
            //System.out.println("圆不在矩形内");
            return false;
        }
    }

    /**
     * 描述：获取两点间的距离.
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double getDistance(double x1, double y1, double x2, double y2) {
        double x = x1 - x2;
        double y = y1 - y2;
        return Math.sqrt(x * x + y * y);
    }


    /**
     * 矩形碰撞检测 参数为x,y,width,height
     *
     * @param x1 第一个矩形的x
     * @param y1 第一个矩形的y
     * @param w1 第一个矩形的w
     * @param h1 第一个矩形的h
     * @param x2 第二个矩形的x
     * @param y2 第二个矩形的y
     * @param w2 第二个矩形的w
     * @param h2 第二个矩形的h
     * @return 是否碰撞
     */
    public static boolean isRectCollision(float x1, float y1, float w1, float h1,
                                          float x2, float y2, float w2, float h2) {
        if (x2 > x1 && x2 > x1 + w1) {
            return false;
        } else if (x2 < x1 && x2 < x1 - w2) {
            return false;
        } else if (y2 > y1 && y2 > y1 + h1) {
            return false;
        } else if (y2 < y1 && y2 < y1 - h2) {
            return false;
        } else {
            return true;
        }
    }

    public static void main(String[] args) {
        //申购数量
        BigDecimal quantityApply = new BigDecimal("1000.0");
        //总申购金额
        BigDecimal applyAmount = new BigDecimal("6666.51");
        //申购价
        BigDecimal applyPrice = applyAmount.divide(quantityApply, 3, BigDecimal.ROUND_FLOOR);

        StringBuffer numsb = new StringBuffer();
        numsb.append(quantityApply.toString() + "").append("\n");
        numsb.append(applyAmount.toString() + "").append("\n");
        numsb.append(applyPrice.toString() + "").append("\n");
        System.out.println(numsb.toString());
    }

}
