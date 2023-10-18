package core.colin.basic.utils.audio;

import android.content.Context;
import android.media.AudioManager;

import androidx.core.os.EnvironmentCompat;


public class AudioUtil {

    public static boolean isWiredHeadsetOn(Context context) {
        return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).isWiredHeadsetOn();
    }

    public static boolean isMusicActive(Context context) {
        return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).isMusicActive();
    }

    public static void setMicrophoneMute(Context context, boolean z) {
        ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).setMicrophoneMute(z);
    }

    public static boolean isMicrophoneMute(Context context) {
        return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).isMicrophoneMute();
    }

    public static void setMode(Context context, int mode) {
        ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).setMode(mode);
    }

    public static int getMode(Context context) {
        return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).getMode();
    }

    public static String getModeString(Context context) {
        int mode = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).getMode();
        if (mode == 0) {
            return "MODE_NORMAL";
        }
        if (mode == 1) {
            return "MODE_RINGTONE";
        }
        if (mode == 2) {
            return "MODE_IN_CALL";
        }
        return mode == 3 ? "MODE_IN_COMMUNICATION" : EnvironmentCompat.MEDIA_UNKNOWN;
    }

    public static void setRingerMode(Context context, int ringerMode) {
        ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).setRingerMode(ringerMode);
    }

    public static int getRingerMode(Context context) {
        return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).getRingerMode();
    }

    public static String getRingerModeString(Context context) {
        int ringerMode = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).getRingerMode();
        if (ringerMode == 2) {
            return "RINGER_MODE_NORMAL";
        }
        if (ringerMode == 0) {
            return "RINGER_MODE_SILENT";
        }
        return ringerMode == 1 ? "RINGER_MODE_VIBRATE" : EnvironmentCompat.MEDIA_UNKNOWN;
    }

    public static void adjustStreamVolume(Context context, int streamType, int direction, int flags) {
        ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).adjustStreamVolume(streamType, direction, flags);
    }

    public static void adjustVolume(Context context, int direction, int flags) {
        ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).adjustVolume(direction, flags);
    }

    public static void setSystemVolume(Context context, int volume) {
        ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .setStreamVolume(AudioManager.STREAM_SYSTEM, volume, 5);
    }

    public static void setSystemVolumeToMaximum(Context context) {
        ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .setStreamVolume(AudioManager.STREAM_SYSTEM, getSystemMaxVolume(context), 5);
    }

    public static int getSystemMaxVolume(Context context) {
        return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
    }

    public static int getSystemVolume(Context context) {
        return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .getStreamVolume(AudioManager.STREAM_SYSTEM);
    }

    public static void setAlermVolume(Context context, int volume) {
        ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .setStreamVolume(AudioManager.STREAM_ALARM, volume, 5);
    }

    public static int getAlermMaxVolume(Context context) {
        return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .getStreamMaxVolume(AudioManager.STREAM_ALARM);
    }

    public static int getAlermVolume(Context context) {
        return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .getStreamVolume(AudioManager.STREAM_ALARM);
    }

    public static void setMediaVolume(Context context, int volume) {
        ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .setStreamVolume(AudioManager.STREAM_MUSIC, volume, 5);
    }

    public static void setMediaVolumeToMaximum(Context context) {
        ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .setStreamVolume(AudioManager.STREAM_MUSIC, getMediaMaxVolume(context), 5);
    }

    public static int getMediaMaxVolume(Context context) {
        return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public static int getMediaVolume(Context context) {
        return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public static void setCallVolume(Context context, int volume) {
        ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .setStreamVolume(AudioManager.STREAM_VOICE_CALL, volume, 0);
    }

    public static int getCallMaxVolume(Context context) {
        return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
    }

    public static int getCallVolume(Context context) {
        return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .getStreamVolume(AudioManager.STREAM_VOICE_CALL);
    }

    public static void setSpeakerStatus(Context context, boolean speakerphoneOn) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (speakerphoneOn) {
            //是否外放
            audioManager.setSpeakerphoneOn(true);
            audioManager.setMode(AudioManager.MODE_NORMAL);

        } else {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            audioManager.setSpeakerphoneOn(false);
            audioManager.setRouting(0, 1, -1);
        }

    }
}
