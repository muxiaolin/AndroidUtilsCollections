package core.colin.basic.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 键盘功能集合
 */

public class KeyboardUtils {

    /**
     * 隐藏虚拟键盘
     *
     * @param context
     */
    public static void hideSoftInput(Context context) {
        Activity activity = Utils.getActivity(context);
        hideSoftInput(activity);
    }


    /**
     * 隐藏虚拟键盘
     *
     * @param activity
     */
    public static void hideSoftInput(Activity activity) {
        if (activity != null && com.blankj.utilcode.util.KeyboardUtils.isSoftInputVisible(activity)) {
            com.blankj.utilcode.util.KeyboardUtils.hideSoftInput(activity);
        }
    }


    /**
     * 隐藏虚拟键盘
     *
     * @param view
     */
    public static void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

        }
    }


    // 隐藏虚拟键盘
//    public static void hideSoftInput2(View view) {
//        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (null != imm) {
//            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        }
//    }

    // 隐藏虚拟键盘
//    public static void hideSoftInput(Context mContext, IBinder iBinder) {
//        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(iBinder, 0);
//    }

    /**
     * 显示虚拟键盘
     */
    public static void show(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    /**
     * 显示虚拟键盘
     */
    public static void openKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 显示虚拟键盘
     *
     * @param editText 输入框
     */
    public static void openKeyboard(EditText editText) {
        if (editText != null) {
            InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.RESULT_SHOWN);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    /**
     *
     */
    public static void toggleKeyboard(Context context) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }


    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        //显示软键盘
//        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        //如果上面的代码没有弹出软键盘 可以使用下面另一种方式
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }
}
