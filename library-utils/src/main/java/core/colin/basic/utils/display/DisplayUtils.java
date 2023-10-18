package core.colin.basic.utils.display;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Point;
import android.graphics.Shader;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import java.lang.reflect.Method;

import core.colin.basic.utils.Utils;

/**
 * 显示相关帮助类
 */
public final class DisplayUtils {

    public static int getColorFromAttrRes(int attrRes, int defValue, Context context) {
        int[] attrs = new int[]{attrRes};
        TypedArray a = context.obtainStyledAttributes(attrs);
        int color = a.getColor(0, defValue);
        a.recycle();
        return color;
    }

    public static int getResIdFromAttrRes(int attrRes, int defValue, Context context) {
        int[] attrs = new int[]{attrRes};
        TypedArray a = context.obtainStyledAttributes(attrs);
        int resId = a.getResourceId(0, defValue);
        a.recycle();
        return resId;
    }

    public static int getWidthExcludingPadding(View view) {
        return Math.max(0, view.getWidth() - view.getPaddingLeft() - view.getPaddingRight());
    }

    public static int getHeightExcludingPadding(View view) {
        return Math.max(0, view.getHeight() - view.getPaddingTop() - view.getPaddingBottom());
    }

    private static boolean hasSwDp(int dp, Context context) {
        return context.getResources().getConfiguration().smallestScreenWidthDp >= dp;
    }

    public static boolean hasSw600Dp(Context context) {
        return hasSwDp(600, context);
    }

    private static boolean hasWDp(int dp, Context context) {
        return context.getResources().getConfiguration().screenWidthDp >= dp;
    }

    public static boolean hasW600Dp(Context context) {
        return hasWDp(600, context);
    }

    public static boolean hasW960Dp(Context context) {
        return hasWDp(960, context);
    }

    /**
     * 计算颜色
     *
     * @param color color值
     * @param alpha alpha值
     * @return 最终的状态栏颜色
     */
    public static int calculateAlphaColor(@ColorInt int color, @IntRange(from = 0, to = 255) int alpha) {
        if (alpha == 0) {
            return color;
        }
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }

    /**
     * 计算颜色
     *
     * @param color color值
     * @param alpha alpha值
     * @return 最终的状态栏颜色
     */
    public static int alphaColor(@ColorInt int color, @IntRange(from = 0, to = 255) int alpha) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    /**
     * 计算颜色
     *
     * @param color color值
     * @param alpha alpha值[0-1]
     * @return 最终的状态栏颜色
     */
    public static int alphaColor(@ColorInt int color, @FloatRange(from = 0, to = 1) float alpha) {
        return alphaColor(color, (int) (alpha * 255));
    }

    /**
     * 根据fraction值来计算当前的颜色
     *
     * @param colorFrom 起始颜色
     * @param colorTo   结束颜色
     * @param fraction  变量
     * @return 当前颜色
     */
    public static int changingColor(@ColorInt int colorFrom, @ColorInt int colorTo, @FloatRange(from = 0, to = 1) float fraction) {
        int redStart = Color.red(colorFrom);
        int blueStart = Color.blue(colorFrom);
        int greenStart = Color.green(colorFrom);
        int alphaStart = Color.alpha(colorFrom);

        int redEnd = Color.red(colorTo);
        int blueEnd = Color.blue(colorTo);
        int greenEnd = Color.green(colorTo);
        int alphaEnd = Color.alpha(colorTo);

        int redDifference = redEnd - redStart;
        int blueDifference = blueEnd - blueStart;
        int greenDifference = greenEnd - greenStart;
        int alphaDifference = alphaEnd - alphaStart;

        int redCurrent = (int) (redStart + fraction * redDifference);
        int blueCurrent = (int) (blueStart + fraction * blueDifference);
        int greenCurrent = (int) (greenStart + fraction * greenDifference);
        int alphaCurrent = (int) (alphaStart + fraction * alphaDifference);

        return Color.argb(alphaCurrent, redCurrent, greenCurrent, blueCurrent);
    }

    /**
     * 给color添加透明度
     *
     * @param alpha     透明度 0f～1f
     * @param baseColor 基本颜色
     * @return
     */
    public static int getColorWithAlpha(float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        return a + rgb;
    }


    /**
     * 检测是否有虚拟导航栏
     */
    public boolean hasNavigationBar() {
        boolean hasNavigationBar = false;
        Resources rs = Utils.getAppContext().getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasNavigationBar;
    }


    /**
     * 获取导航栏高度
     */
    public static int getNavigationBarHeight() {
        int rid = Utils.getAppContext().getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid != 0) {
            int resourceId = Utils.getAppContext().getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return Utils.getAppContext().getResources().getDimensionPixelSize(resourceId);
            }
        }
        return 0;
    }


    public Point getAppUsableScreenSize() {
        WindowManager windowManager;
        windowManager = (WindowManager) Utils.getAppContext().getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point(0, 0);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            display.getSize(size);
        }
        return size;
    }


    public static View inflate(int resource, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
    }

    public static View inflateInto(int resource, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(resource, parent);
    }

    public static boolean isInLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static boolean isVisible(View view) {
        return view.getVisibility() == VISIBLE;
    }

    public static void replaceChild(ViewGroup viewGroup, View oldChild, View newChild) {
        int index = viewGroup.indexOfChild(oldChild);
        viewGroup.removeViewAt(index);
        viewGroup.addView(newChild, index);
    }

    public static void setHeight(View view, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams.height == height) {
            return;
        }
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
    }

    public static void setSize(View view, int size) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams.width == size && layoutParams.height == size) {
            return;
        }
        layoutParams.width = size;
        layoutParams.height = size;
        view.setLayoutParams(layoutParams);
    }

    public static void setLayoutFullscreen(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);}
    }

    public static void setLayoutFullscreen(Activity activity) {
        setLayoutFullscreen(activity.getWindow().getDecorView());
    }

//    public static void setTextViewBold(TextView textView, boolean bold) {
//        Typeface typeface = textView.getTypeface();
//        if (typeface.isBold() == bold) {
//            return;
//        }
//
//        int style = textView.getTypeface().getStyle();
//        if (bold) {
//            style |= Typeface.BOLD;
//        } else {
//            style &= ~Typeface.BOLD;
//        }
//        // Workaround insane behavior in TextView#setTypeface(Typeface, int).
//        if (style > 0) {
//            textView.setTypeface(typeface, style);
//        } else {
//            textView.setTypeface(Typeface.create(typeface, style), style);
//        }
//    }
//
//    public static void setTextViewItalic(TextView textView, boolean italic) {
//        Typeface typeface = textView.getTypeface();
//        if (typeface.isItalic() == italic) {
//            return;
//        }
//
//        int style = textView.getTypeface().getStyle();
//        if (italic) {
//            style |= Typeface.ITALIC;
//        } else {
//            style &= ~Typeface.ITALIC;
//        }
//        // Workaround insane behavior in TextView#setTypeface(Typeface, int).
//        if (style > 0) {
//            textView.setTypeface(typeface, style);
//        } else {
//            textView.setTypeface(Typeface.create(typeface, style), style);
//        }
//    }


    public static void setGoneText(TextView textView, CharSequence text) {
        textView.setText(text);
        if (TextUtils.isEmpty(text)) {
            textView.setVisibility(GONE);
        } else {
            textView.setVisibility(VISIBLE);
        }
    }

    public static void setInvisibleText(TextView textView, CharSequence text) {
        textView.setText(text);
        if (TextUtils.isEmpty(text)) {
            textView.setVisibility(View.INVISIBLE);
        } else {
            textView.setVisibility(VISIBLE);
        }
    }

    public static void setVisibleOrGone(View view, boolean visible) {
        view.setVisibility(visible ? VISIBLE : GONE);
    }

    public static void setVisibleOrInvisible(View view, boolean visible) {
        view.setVisibility(visible ? VISIBLE : View.INVISIBLE);
    }

    public static void setWidth(View view, int width) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams.width == width) {
            return;
        }
        layoutParams.width = width;
        view.setLayoutParams(layoutParams);
    }

    /**
     * 测量这个view
     * 最后通过getMeasuredWidth()获取宽度和高度.
     *
     * @param view 要测量的view
     * @return 测量过的view
     */
    public static void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        view.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * 获得这个View的宽度
     * 测量这个view，最后通过getMeasuredWidth()获取宽度.
     *
     * @param view 要测量的view
     * @return 测量过的view的宽度
     */
    public static int getViewWidth(View view) {
        measureView(view);
        return view.getMeasuredWidth();
    }

    /**
     * 获得这个View的高度
     * 测量这个view，最后通过getMeasuredHeight()获取高度.
     *
     * @param view 要测量的view
     * @return 测量过的view的高度
     */
    public static int getViewHeight(View view) {
        measureView(view);
        return view.getMeasuredHeight();
    }

    /**
     * 从父亲布局中移除自己
     *
     * @param v
     */
    public static void removeSelfFromParent(View v) {
        ViewParent parent = v.getParent();
        if (parent != null) {
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(v);
            }
        }
    }

    //使用工具类自行计算（非控件固定宽度）
    public static void adjustTvTextSize(TextView tv, int maxWidth, String text) {
        tv.setText(text);
        int avaiWidth = maxWidth - tv.getPaddingLeft() - tv.getPaddingRight();
        if (avaiWidth <= 0) {
            return;
        }
        TextPaint textPaintClone = new TextPaint(tv.getPaint());
        float trySize = textPaintClone.getTextSize();

        while (textPaintClone.measureText(text) > avaiWidth) {
            trySize--;
            textPaintClone.setTextSize(trySize);
        }
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize);
    }

    /**
     * 字体颜色渐变
     *
     * @param textView
     */
    public static void setTextGradientTopToBottom(TextView textView, int startColor, int endColor) {
        float x1 = textView.getPaint().measureText(textView.getText().toString());//测量文本 宽度
        float y1 = textView.getPaint().getTextSize();//测量文本 高度
//        LinearGradient leftToRightLG = new LinearGradient(0, 0, x1, 0, startColor, endColor, Shader.TileMode.CLAMP);//从左到右渐变
        LinearGradient topToBottomLG = new LinearGradient(0, 0, 0, y1, startColor, endColor, Shader.TileMode.CLAMP);//从上到下渐变
        textView.getPaint().setShader(topToBottomLG);
        textView.invalidate();
    }

    /**
     * 字体颜色渐变
     *
     * @param textView
     */
    public static void setTextGradientLeftToRight(TextView textView, int startColor, int endColor) {
        float x1 = textView.getPaint().measureText(textView.getText().toString());//测量文本 宽度
        float y1 = textView.getPaint().getTextSize();//测量文本 高度
        LinearGradient leftToRightLG = new LinearGradient(0, 0, x1, 0, startColor, endColor, Shader.TileMode.CLAMP);//从左到右渐变
//        LinearGradient topToBottomLG = new LinearGradient(0, 0, 0, y1, startColor, endColor, Shader.TileMode.CLAMP);//从上到下渐变
        textView.getPaint().setShader(leftToRightLG);
        textView.invalidate();
    }


    public static void makeRadioButtonWithGroup(@Nullable RadioButton defaultSelect,
                                                final SimpleListener<RadioButton> listener,
                                                final RadioButton... radioButtons) {
        if (radioButtons == null || radioButtons.length == 0) {
            return;
        }
        for (RadioButton radioButton : radioButtons) {
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (listener != null) {
                            listener.onAction((RadioButton) buttonView);
                        }
                        for (RadioButton button : radioButtons) {
                            if (button.getId() != buttonView.getId()) {
                                button.setChecked(false);
                            }
                        }
                    }
                }
            });
        }
        for (RadioButton radioButton : radioButtons) {
            if (defaultSelect == null) {
                radioButton.setChecked(false);
            } else {
                if (radioButton.getId() == defaultSelect.getId()) {
                    radioButton.setChecked(true);
                    break;
                }
            }
        }
    }

    public interface SimpleListener<E> {
        void onAction(E data);
    }


}
