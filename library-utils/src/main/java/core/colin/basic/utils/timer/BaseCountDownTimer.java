package core.colin.basic.utils.timer;

import android.os.CountDownTimer;

/**
 * 描述：倒计时
 */
public abstract class BaseCountDownTimer extends CountDownTimer {

    private boolean mStart = false;

    public BaseCountDownTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture + 499, countDownInterval);
    }

    @Override
    public final void onTick(long millisUntilFinished) {
        if (!mStart) {
            mStart = true;
            onTimerStart(millisUntilFinished);
        }
        onTimerTick(millisUntilFinished);
    }

    @Override
    public final void onFinish() {
        mStart = false;
        onTimerFinish();
    }


    public boolean isStart() {
        return mStart;
    }

    public void finish() {
        onFinish();
        cancel();
    }


    protected abstract void onTimerStart(long millisUntilFinished);

    protected abstract void onTimerTick(long millisUntilFinished);

    protected abstract void onTimerFinish();
}

