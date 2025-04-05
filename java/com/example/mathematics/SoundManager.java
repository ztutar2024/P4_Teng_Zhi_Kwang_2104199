package com.example.mathematics;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundManager {
    private static MediaPlayer clickSound;

    public static void playClickSound(Context context) {
        if (clickSound != null) {
            clickSound.release();
        }
        clickSound = MediaPlayer.create(context, R.raw.button_click);
        clickSound.start();
    }
} 