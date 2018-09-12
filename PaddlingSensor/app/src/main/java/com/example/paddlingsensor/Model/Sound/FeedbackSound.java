package com.example.paddlingsensor.Model.Sound;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.example.paddlingsensor.Model.Queue.IMUQueue;
import com.example.paddlingsensor.Model.Queue.IMUQueueModel;
import com.example.paddlingsensor.R;

/**
 * Created by Asad Hussain.
 */

public class FeedbackSound extends Thread {

    private Context context;
    private SoundPool soundPool;
    private int sound;

    public FeedbackSound(Context context) {
        this.context = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        //sound = soundPool.load(context, R.raw.sound_to_be_played, 1);
    }

    /**
     * @param loops amount of loops the sound should be played, 0 för playing one time, 1 for playing 2 times etc.
     */
    public void play(int loops) {

        if (loops <= 2 || loops >= 0) {
            soundPool.play(sound, 1, 1, 1, loops, 4);
        }
    }

    public boolean destroySound() {
        return soundPool.unload(sound);
    }
}
