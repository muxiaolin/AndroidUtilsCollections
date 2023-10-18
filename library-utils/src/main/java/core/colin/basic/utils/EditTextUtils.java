package core.colin.basic.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import core.colin.basic.utils.filter.EditTextJudgeNumberWatcher;
import core.colin.basic.utils.filter.InputFilterMinMax;

/**
 * 描述：
 */
public class EditTextUtils {

    // 居民身份证号的组成元素
    static String[] IDCARD = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "x", "X",};

    public static void clearFocusView(Context context) {
       clearFocusView(Utils.getActivity(context));
    }

    public static void clearFocusView(Activity activity) {
        if (activity == null) {
            return;
        }
        View rootview = activity.getWindow().getDecorView();
        if (rootview == null) {
            return;
        }
        View focusView = rootview.findFocus();
        if (focusView instanceof EditText) {
            ((EditText) focusView).clearFocus();
        }
    }

    public static void requestFocusView(Activity activity) {
        if (activity == null) {
            return;
        }
        View rootview = activity.getWindow().getDecorView();
        if (rootview == null) {
            return;
        }
        View focusView = rootview.findFocus();
        if (focusView instanceof EditText) {
            focusView.requestFocus();
            ((EditText) focusView).setSelection(((EditText) focusView).length());
        }
    }

    public static void requestEditTextFocus(final EditText editText) {
        editText.setSelection(editText.getText().length());
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
            }
        }, 500);
    }

    public static void setTextWithSelection(EditText editText, CharSequence text) {
        editText.setText(text);
        if (editText.getText() != null) {
            editText.setSelection(editText.getText().length());
        }
    }

    /**
     * 禁止EditText弹出软件盘，光标依然正常显示。
     */
    public static void disableShowSoftInput(EditText editText) {
        Class<EditText> cls = EditText.class;
        Method method;
        try {
            method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            method.setAccessible(true);
            method.invoke(editText, false);
        } catch (Exception ignore) {
        }
        try {
            method = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
            method.setAccessible(true);
            method.invoke(editText, false);
        } catch (Exception ignore) {
        }
    }

    public static void setEditTextDigitsKey(@NonNull EditText editText, @NonNull String keys) {
        editText.setKeyListener(DigitsKeyListener.getInstance(keys));
    }

    /**
     * 设置输入框只能输入身份证, 没有用
     *
     * @param editText
     */
    public static void setEditTextIdCardCheck(EditText editText) {
        // 在这里设置才有用！[对EditText可以输入的字符进行了限制][使用Android原生的键盘会停留在数字键盘无切换到英文键盘][第三方的键盘正常使用]
        // et_idcard.setInputType(InputType.TYPE_CLASS_TEXT);
        // et_idcard.setKeyListener(DigitsKeyListener.getInstance("1234567890Xx"));
        final List<String> idCardList = Arrays.asList(IDCARD);
        InputFilter inputFilter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                // 返回空字符串，就代表匹配不成功，返回null代表匹配成功
                for (int i = 0; i < source.toString().length(); i++) {
                    if (!idCardList.contains(String.valueOf(source.charAt(i)))) {
                        return "";
                    }
                    if (editText.getText().toString().length() < 17) {
                        if ("x".equals(String.valueOf(source.charAt(i))) || "X".equals(String.valueOf(source.charAt(i)))) {
                            return "";
                        }
                    }
                }
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18), inputFilter});// 长度的限制和字符的限制
    }

    //将EditText套进NestedScrollView的情况下,EditText输入了多行内容后,无法触摸滚动到第一行
    public static void initScrollEditText(EditText editText, NestedScrollView scrollView) {
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //canScrollVertically()方法为判断指定方向上是否可以滚动,参数为正数或负数,负数检查向上是否可以滚动,正数为检查向下是否可以滚动
                if (editText.canScrollVertically(1) || editText.canScrollVertically(-1)) {
                    scrollView.requestDisallowInterceptTouchEvent(true);//requestDisallowInterceptTouchEvent();
                    // 要求父类布局不在拦截触摸事件
                    if (event.getAction() == MotionEvent.ACTION_UP) { //判断是否松开
                        scrollView.requestDisallowInterceptTouchEvent(false); //requestDisallowInterceptTouchEvent();
                        // 让父类布局继续拦截触摸事件
                    }
                }
                return false;
            }
        });
    }

    @SuppressLint("SoonBlockedPrivateApi")
    public static void changeCursorColor(TextView target, int colorInt) {
        try {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(target);
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(target);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);
            Drawable[] drawables = new Drawable[1];
            drawables[0] = target.getContext().getResources().getDrawable(mCursorDrawableRes);
            drawables[0].setColorFilter(colorInt, PorterDuff.Mode.SRC_IN);
            fCursorDrawable.set(editor, drawables);
        } catch (Exception ignored) {
        }
    }

    public static void setEditTextLimit(@NonNull EditText editText, int pointLength, int wholeLength) {
        editText.addTextChangedListener(new EditTextJudgeNumberWatcher(editText, pointLength, wholeLength));
    }

    /**
     * 限制输入的最大值和最小值以及小数点值2位数
     *
     * @param editText
     */
    public static void setEditTextMinMaxLimit(@NonNull EditText editText, float min, float max) {
        editText.setFilters(new InputFilter[]{new InputFilterMinMax(min, max)});
    }

    /**
     * 设置限制输入框最大字符长度
     *
     * @param editText
     * @param length
     */
    public static void setEditTextMaxLength(@NonNull EditText editText, @IntRange(from = 1) int length) {
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
    }


    /**
     * 输入框划分分组
     * 如银行卡号，邀请码等
     *
     * @param editText   输入框
     * @param groupCount 每个分组的字符个数
     * @param separator  分割符号
     */
    public static void divideEditTextWithGroup(@NonNull EditText editText, @IntRange(from = 1) int groupCount,
                                               char separator) {
        editText.addTextChangedListener(new TextWatcher() {
            int beforeTextLength = 0;
            int onTextLength = 0;
            boolean isChanged = false;
            int cursorLocation = 0;
            private char[] tempChar;
            private final StringBuffer buffer = new StringBuffer();
            int separatorNum = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextLength = s.length();
                if (buffer.length() > 0) {
                    buffer.delete(0, buffer.length());
                }
                separatorNum = 0;
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == separator) {
                        separatorNum++;
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onTextLength = s.length();
                buffer.append(s.toString());
                if (onTextLength == beforeTextLength || onTextLength <= 3 || isChanged) {
                    isChanged = false;
                    return;
                }
                isChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isChanged) {
                    cursorLocation = editText.getSelectionEnd();
                    int index = 0;
                    while (index < buffer.length()) {
                        if (buffer.charAt(index) == separator) {
                            buffer.deleteCharAt(index);
                        } else {
                            index++;
                        }
                    }
                    index = 0;
                    int separatorNum = 0;
                    while (index < buffer.length()) {
                        if ((index + 1) / (groupCount + 1) == 0) {
                            buffer.insert(index, separator);
                            separatorNum++;
                        }
                        index++;
                    }
                    if (separatorNum > this.separatorNum) {
                        cursorLocation += (separatorNum - this.separatorNum);
                    }
                    tempChar = new char[buffer.length()];
                    buffer.getChars(0, buffer.length(), tempChar, 0);
                    String str = buffer.toString();
                    if (cursorLocation > str.length()) {
                        cursorLocation = str.length();
                    } else if (cursorLocation < 0) {
                        cursorLocation = 0;
                    }
                    editText.setText(str);
                    Editable editable = editText.getText();
                    Selection.setSelection(editable, cursorLocation);
                    isChanged = false;
                }
            }
        });
    }


    /**
     * 设置限制输入框最大字符长度, 并且显示已输入个数
     *
     * @param inputEditText
     * @param numTextView
     * @param maxNum
     */
    public static void makeEditTextWithShowInputWordsNumber(EditText inputEditText, TextView numTextView, int maxNum) {
        if (numTextView != null) {
            numTextView.setText(inputEditText.getText().toString().length() + "/" + maxNum);
        }
        inputEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxNum)});
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (numTextView != null) {
                    numTextView.setText(editable.toString().length() + "/" + maxNum);
                }
            }
        });
    }

    public static void makeEditTextWithAmountMaxLength(final EditText edt, int maxLength) {
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (TextUtils.isEmpty(text)) {
                    return;
                }
                if (text.startsWith("00")) {
                    edt.setText(text.replace("00", "0"));//去除00开头
                }
                if (text.startsWith("0") && text.length() >= 2 && !".".equals(text.charAt(1) + "")) {
                    edt.setText(text.substring(1));//替换整数0开头 输入01显示1
                }
                if (text.startsWith(".")) {
                    edt.setText(String.format("0%s", text));//.开头替换为0.开头
                }
                if (text.contains(".") && (text.length() - text.indexOf(".")) > 3) {
                    edt.setText(text.substring(0, text.length() - 1));//保留两位小数
                }
                if (text.length() > maxLength) {
                    edt.setText(text.substring(0, text.length() - 1));//最大长度限制
                }
                edt.setSelection(edt.getText().length());//不能用 text.length() 否则输入00索引异常
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


//    public static void makeEditTextWithAmountInput(final EditText editText, float minMoney, float allMoney) {
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.toString().contains(".")) {
//                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
//                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
//                        editText.setText(s);
//                        editText.setSelection(s.length());
//                    }
//                    if (".".equals(s.toString().trim())) {
//                        s = "0" + s;
//                        editText.setText(s);
//                        editText.setSelection(2);
//                    }
//                    if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
//                        if (!".".equals(s.toString().substring(1, 2))) {
//                            editText.setText(s.subSequence(0, 1));
//                            editText.setSelection(1);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (!"".equals(s.toString()) && !".".equals(s.toString())) {
//                    if (Float.parseFloat(s.toString()) > allMoney) {
//                        setTextWithSelection(editText, String.valueOf(allMoney));
//                    }
//                }
//            }
//        });
//    }


    /**
     * 设置输入框点击提交后，数据非空
     *
     * @param submitView
     * @param editTexts
     */
    public static void bindEditTextAndSubmitViewWithNotEmpty(final View submitView, final TextView... editTexts) {
        if (editTexts == null || editTexts.length == 0) {
            return;
        }
        for (TextView editText : editTexts) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    boolean allInputted = true;
                    for (TextView et : editTexts) {
                        final String text = et.getText().toString().trim();
                        final boolean empty = TextUtils.isEmpty(text);
                        if (empty) {
                            allInputted = false;
                            break;
                        }
                    }
                    if (allInputted) {
                        submitView.setEnabled(true);
                        submitView.setAlpha(1);
                    } else {
                        submitView.setEnabled(false);
                        submitView.setAlpha(0.3f);
                    }
                }
            });
        }
    }


}
