package core.colin.basic.utils.display;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SizeUtils;

/**
 * Author: PL
 * Date: 2022/10/19
 * Desc:
 */
public class WatermarkDrawable extends Drawable {

    private Paint mPaint;
    /**
     * 水印文本
     */
    public String mText = "";
    /**
     * 字体颜色，十六进制形式，例如：0xAEAEAEAE
     */
    public int mTextColor = 0xAEAEAEAE;
    /**
     * 字体大小，单位为sp
     */
    public float mTextSize = 18;
    /**
     * 旋转角度
     */
    public float mRotation = -25;

    public WatermarkDrawable() {
        this("测试");
    }

    public WatermarkDrawable(String text) {
        mPaint = new Paint();
        mText = text;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        int width = getBounds().right;
        int height = getBounds().bottom;
        int diagonal = (int) Math.sqrt(width * width + height * height); // 对角线的长度
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(SizeUtils.sp2px(mTextSize));
        mPaint.setAntiAlias(true);
        float textWidth = mPaint.measureText(mText);

        canvas.drawColor(0x00000000);
        canvas.rotate(mRotation);

        int index = 0;
        float fromX;
        // 以对角线的长度来做高度，这样可以保证竖屏和横屏整个屏幕都能布满水印
        for (int positionY = diagonal / 10; positionY <= diagonal; positionY += diagonal / 10) {
            fromX = -width + (index++ % 2) * textWidth; // 上下两行的X轴起始点不一样，错开显示
            for (float positionX = fromX; positionX < width; positionX += textWidth * 2) {
                canvas.drawText(mText, positionX, positionY, mPaint);
            }
        }

        canvas.save();
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
