package core.colin.basic.utils.filter;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Author: PL
 * Date: 2022/8/25
 * Desc: 输入框中的内容限制（最大：小数点前五位，小数点后2位）
 */
public class EditTextJudgeNumberWatcher implements TextWatcher {

    private final EditText editText;
    private int pointLength = 2;
    private int wholeLength = 5;

    public EditTextJudgeNumberWatcher(EditText editText) {
        this.editText = editText;
    }


    public EditTextJudgeNumberWatcher(EditText editText, int pointLength, int wholeLength) {
        this.editText = editText;
        this.pointLength = pointLength;
        this.wholeLength = wholeLength;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String text = s.toString();
        onTextChanged(editText, text);
    }

    @Override
    public void afterTextChanged(Editable editable) {
        afterTextChanged(editable, editText, pointLength, wholeLength);
    }

    public static void onTextChanged(EditText editText, String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        if (content.startsWith("00")) {
            editText.setText(content.replace("00", "0"));//去除00开头
        }
        if (content.startsWith("0") && content.length() >= 2 && !".".equals(content.charAt(1) + "")) {
            editText.setText(content.substring(1));//替换整数0开头 输入01显示1
        }
        if (content.startsWith(".")) {
            editText.setText(String.format("0%s", content));//.开头替换为0.开头
        }
        editText.setSelection(editText.getText().length());//不能用 content.length() 否则输入00索引异常
    }

    /**
     * 输入框中的内容限制（最大：小数点前五位，小数点后2位）
     *
     * @param pointLength 小数部分长度
     * @param wholeLength 整数部分的长度
     */
    public static void afterTextChanged(Editable edt, EditText editText, int pointLength, int wholeLength) {
        String temp = edt.toString();
        int posDot = temp.indexOf(".");//返回指定字符在此字符串中第一次出现处的索引
        int index = editText.getSelectionStart();//获取光标位置
        //  if (posDot == 0) {//必须先输入数字后才能输入小数点
        //  edt.delete(0, temp.length());//删除所有字符
        //  return;
        //  }
        if (posDot < 0) {//不包含小数点
            if (temp.length() <= wholeLength) {
                return;//小于五位数直接返回
            } else {
                edt.delete(index - 1, index);//删除光标前的字符
                return;
            }
        }
        if (posDot > wholeLength) {//小数点前大于5位数就删除光标前一位
            edt.delete(index - 1, index);//删除光标前的字符
            return;
        }
        if (temp.length() - posDot - 1 > pointLength)//如果包含小数点
        {
            edt.delete(index - 1, index);//删除光标前的字符
            return;
        }
    }
}