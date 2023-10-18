package core.colin.basic.utils.filter;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Author: PL
 * Date: 2022/8/25
 * Desc:
 */
public class InputFilterMinMax implements InputFilter {
    private final float min, max;

    public InputFilterMinMax(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax(String min, String max) {
        this.min = Float.parseFloat(min);
        this.max = Float.parseFloat(max);
    }


    /**
     * @param source 输入的文字
     * @param start  输入-0，删除-0
     * @param end    输入-文字的长度，删除-0
     * @param dest   原先显示的内容
     * @param dstart 输入-原光标位置，删除-光标删除结束位置
     * @param dend   输入-原光标位置，删除-光标删除开始位置
     * @return
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            //限制小数点位数
            if (source.equals(".") && dest.toString().length() == 0) {
                return "0.";
            }
            if (dest.toString().contains(".")) {
                int index = dest.toString().indexOf(".");
                int mlength = dest.toString().substring(index).length();
                if (mlength == 3) {
                    return "";
                }
            }
            //限制大小
            float input = Float.parseFloat(dest.toString() + source.toString());
            if (isInRange(min, max, input)) {
                return null;
            }
        } catch (Exception nfe) {

        }
        return "";
    }

    private boolean isInRange(float a, float b, float c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}