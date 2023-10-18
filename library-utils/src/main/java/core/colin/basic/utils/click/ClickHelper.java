package core.colin.basic.utils.click;

import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;


public class ClickHelper {

    private static long DELAY = 500L;
    private static long lastTime = 0L;
    private static List<Integer> viewIds = null;
    private static final int SAME_VIEW_SIZE = 10;

    //继续点击
    private final static int COUNTS = 5;// 点击次数
    private final static long DURATION = 1000;// 规定有效时间
    private static long[] mHits = new long[COUNTS];

    //双击退出
    private static final long DOUBLE_BACK_PRESSED_TIME = 2000;
    private static long LAST_BACK_PRESSED_TIME = 0;

    private ClickHelper() {
    }

//    public static void setDelay(long delay) {
//        ClickHelper.DELAY = delay;
//    }
//
//    public static long getDelay() {
//        return DELAY;
//    }


    /**
     * 防止连点
     */
    public static void onlyFirstIgnoreView(final View target, @NonNull final Callback callback) {
        long nowTime = System.currentTimeMillis();
        if (nowTime - lastTime > DELAY) {
            callback.onClick(target);
            lastTime = nowTime;
        }
    }

    /**
     * 防止连点，仅响应设置时长的第一次操作
     */
    public static void onlyFirstSameView(final View target, @NonNull final Callback callback) {
        long nowTime = System.currentTimeMillis();
        int id = target.getId();
        if (viewIds == null) {
            viewIds = new ArrayList<>(SAME_VIEW_SIZE);
        }
        if (viewIds.contains(id)) {
            if (nowTime - lastTime > DELAY) {
                callback.onClick(target);
                lastTime = nowTime;
            }
        } else {
            if (viewIds.size() >= SAME_VIEW_SIZE) {
                viewIds.remove(0);
            }
            viewIds.add(id);
            callback.onClick(target);
            lastTime = nowTime;
        }
    }


    /**
     * 连续点击
     *
     * @param target
     * @param callback
     */
    public static void continuousClick(final View target, @NonNull final Callback callback) {
        //每次点击时，数组向前移动一位
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        //为数组最后一位赋值
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
            mHits = new long[COUNTS];//重新初始化数组
            callback.onClick(target);
        }
    }

    /**
     * 双击退出工具类
     * <p>
     * 在{@link android.app.Activity#onKeyDown(int, KeyEvent)}中调用
     * 如果返回true，则表示双击操作成功，应该调用返回操作，及 return super.onKeyDown(keyCode, event);
     * 如果返回false，则表示双击操作失败，应该拦截本次点击操作事件，及 return false;
     */
    public static boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                long nowBackPressedTime = System.currentTimeMillis();
                if ((nowBackPressedTime - LAST_BACK_PRESSED_TIME) > DOUBLE_BACK_PRESSED_TIME) {
                    LAST_BACK_PRESSED_TIME = nowBackPressedTime;
                    return false;
                }
            }
        }
        return true;
    }

    public interface Callback {
        void onClick(View view);
    }
}
