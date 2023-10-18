package core.colin.basic.utils.display;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.blankj.utilcode.util.SizeUtils;

/**
 * Author: PL
 * Date: 2022/10/21
 * Desc:
 */
public class Watermark {
    /**
     * 水印文本
     */
    private String mText;
    /**
     * 字体颜色，十六进制形式，例如：0x66AEAEAE
     */
    private int mTextColor;
    /**
     * 字体大小，单位为sp
     */
    private float mTextSize;
    /**
     * 旋转角度
     */
    private float mRotation;
    private static Watermark sInstance;


    private Watermark() {
        mText = "";
        mTextColor = 0x66AEAEAE;
        mTextSize = 18;
        mRotation = -25;
    }

    public static Watermark getInstance() {
        if (sInstance == null) {
            synchronized (Watermark.class) {
                sInstance = new Watermark();
            }
        }
        return sInstance;
    }

    /**
     * 设置水印文本
     *
     * @param text 文本
     * @return Watermark实例
     */
    public Watermark setText(String text) {
        mText = text;
        return sInstance;
    }

    /**
     * 设置字体颜色
     *
     * @param color 颜色，十六进制形式，例如：0xAEAEAEAE
     * @return Watermark实例
     */
    public Watermark setTextColor(int color) {
        mTextColor = color;
        return sInstance;
    }

    /**
     * 设置字体大小
     *
     * @param size 大小，单位为sp
     * @return Watermark实例
     */
    public Watermark setTextSize(float size) {
        mTextSize = size;
        return sInstance;
    }

    /**
     * 设置旋转角度
     *
     * @param degrees 度数
     * @return Watermark实例
     */
    public Watermark setRotation(float degrees) {
        mRotation = degrees;
        return sInstance;
    }

    /**
     * 显示水印，铺满整个页面
     *
     * @param activity 活动
     */
    public void show(Activity activity) {
        show(activity, mText);
    }

    /**
     * 显示水印，铺满整个页面
     *
     * @param activity 活动
     * @param text     水印
     */
    public void show(Activity activity, String text) {
        WatermarkDrawable drawable = new WatermarkDrawable();
        drawable.mText = text;
        drawable.mTextColor = mTextColor;
        drawable.mTextSize = mTextSize;
        drawable.mRotation = mRotation;

        ViewGroup rootView = activity.findViewById(android.R.id.content);
        FrameLayout layout = new FrameLayout(activity);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(layoutParams);
        layout.setBackground(drawable);
        rootView.addView(layout);
    }

    /**
     * 显示水印
     *
     * @param activity 活动
     * @param text     水印
     */
    public void showTwo(Activity activity, int tagId, String text) {
        ViewGroup rootView = activity.findViewById(android.R.id.content);
        Object waterTag = rootView.getTag(tagId);
        //已添加水印
        if (waterTag != null && (Boolean) waterTag) {
            return;
        }
        rootView.setTag(tagId, true);

        WatermarkDrawable drawable = new WatermarkDrawable();
        drawable.mText = text;
        drawable.mTextColor = mTextColor;
        drawable.mTextSize = mTextSize;
        drawable.mRotation = mRotation;

//        WindowManager windowManager = activity.getWindowManager();
//        DisplayMetrics metrics = new DisplayMetrics();
//        windowManager.getDefaultDisplay().getMetrics(metrics);

        FrameLayout layout = new FrameLayout(activity);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, SizeUtils.dp2px(20), 0, SizeUtils.dp2px(20));
        layout.setLayoutParams(layoutParams);
        layout.setBackground(drawable);
        rootView.addView(layout);
    }
}
