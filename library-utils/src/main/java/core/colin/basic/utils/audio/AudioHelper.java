package core.colin.basic.utils.audio;

import android.util.Log;

import core.colin.basic.utils.Utils;


/**
 * author : 彭林
 * date   : 2020/6/23
 * desc   :
 */
public class AudioHelper {
    private final String TAG = "AudioHelper";
    private static AudioHelper mInterface;

    public static AudioHelper getInstance() {
        if (mInterface == null) {
            mInterface = new AudioHelper();
        }
        return mInterface;
    }

    public void setCallVolume(int volumePercent) {
        int maxValue = AudioUtil.getCallMaxVolume(Utils.getAppContext());
        int value = (int) Math.ceil((volumePercent) * maxValue * 0.01);
        value = Math.max(value, 0);
        value = Math.min(value, 100);
        AudioUtil.setCallVolume(Utils.getAppContext(), value);
    }

    /**
     * 以0-100为范围，获取当前的音量值
     *
     * @return 获取当前的音量值
     */
    public int get100CallVolume() {
        int maxValue = AudioUtil.getCallMaxVolume(Utils.getAppContext());
        int currValue = AudioUtil.getCallVolume(Utils.getAppContext());
        return 100 * currValue / maxValue;
    }

    public void setMediaVolume(int volumePercent) {
        int maxValue = AudioUtil.getMediaMaxVolume(Utils.getAppContext());
        int value = (int) Math.ceil((volumePercent) * maxValue * 0.01);
        value = Math.max(value, 0);
        value = Math.min(value, 100);
        AudioUtil.setMediaVolume(Utils.getAppContext(), value);
    }

    /**
     * 以0-100为范围，获取当前的音量值
     *
     * @return 获取当前的音量值
     */
    public int get100MediaVolume() {
        int maxValue = AudioUtil.getMediaMaxVolume(Utils.getAppContext());
        int currValue = AudioUtil.getMediaVolume(Utils.getAppContext());
        return 100 * currValue / maxValue;
    }

    public void setMediaVolumeToMaximum() {
        AudioUtil.setMediaVolumeToMaximum(Utils.getAppContext());
    }

    public void setSystemVolumeToMaximum() {
        AudioUtil.setSystemVolumeToMaximum(Utils.getAppContext());
    }


    public void setSpeakerStatus(boolean z) {
        //
        if (AudioUtil.isWiredHeadsetOn(Utils.getAppContext())) {
            AudioUtil.setSpeakerStatus(Utils.getAppContext(), false);
            return;
        }
        AudioUtil.setSpeakerStatus(Utils.getAppContext(), z);
        if (z) {
            Log.i(TAG, "setSpeakerStatus: 打开扬声器!");
        } else {
            Log.i(TAG, "setSpeakerStatus: 关闭扬声器!");
        }
    }


}
