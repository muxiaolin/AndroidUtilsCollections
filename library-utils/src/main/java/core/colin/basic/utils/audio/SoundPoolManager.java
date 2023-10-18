package core.colin.basic.utils.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SoundPoolManager {
    private static float actualVolume = 0.0f;
    private static AudioManager audioManager = null;
    private static int currentPlayStreamId = 0;
    private static SoundPoolManager instance = null;
    private static float maxVolume = 0.0f;
    private static boolean playing = false;
    private static SoundPool soundPool;

    public static final ConcurrentMap<String, SoundResource> sounds = new ConcurrentHashMap<>();
    private static float volume;

    public static SoundPoolManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundPoolManager(context);
        }
        return instance;
    }

    private SoundPoolManager(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        actualVolume = (float) audioManager.getStreamVolume(3);
        maxVolume = (float) audioManager.getStreamMaxVolume(3);
        volume = actualVolume / maxVolume;
        if (Build.VERSION.SDK_INT >= 21) {
            soundPool = new SoundPool.Builder().setMaxStreams(1).build();
        } else {
            soundPool = new SoundPool(1, 3, 0);
        }
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int i, int i2) {
                for (String str : SoundPoolManager.sounds.keySet()) {
                    SoundResource soundResource = (SoundResource) SoundPoolManager.sounds.get(str);
                    if (soundResource != null && i == soundResource.getId()) {
                        soundResource.setLoadedComplete(true);
                        SoundPoolManager.sounds.put(str, soundResource);
                        return;
                    }
                }
            }
        });
    }

    public void loadSound(Context context, String str, int i) {
        sounds.put(str, new SoundResource(soundPool.load(context, i, 1)));
    }

    public boolean play(String str) {
        SoundResource soundResource = (SoundResource) sounds.get(str);
        if (soundResource == null || soundResource.getId() <= 0 || !soundResource.isLoadedComplete() || playing) {
            return false;
        }
        SoundPool soundPool2 = soundPool;
        int id = soundResource.getId();
        float f = volume;
        currentPlayStreamId = soundPool2.play(id, f, f, 1, -1, 1.0f);
        playing = true;
        return false;
    }

    public void pause() {
        if (playing) {
            soundPool.pause(currentPlayStreamId);
        }
    }

    public void resume() {
        if (playing) {
            soundPool.resume(currentPlayStreamId);
        }
    }

    public void stop() {
        if (playing) {
            soundPool.stop(currentPlayStreamId);
            playing = false;
        }
    }

    public void release() {
        if (soundPool != null) {
            for (SoundResource soundResource : sounds.values()) {
                if (soundResource.getId() > 0) {
                    soundPool.unload(soundResource.getId());
                }
            }
            sounds.clear();
            soundPool.release();
            soundPool = null;
        }
        instance = null;
    }
}
