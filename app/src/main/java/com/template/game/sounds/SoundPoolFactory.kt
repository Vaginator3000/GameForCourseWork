package com.template.game.sounds

import android.media.AudioManager
import android.media.SoundPool
import android.os.Build

class SoundPoolFactory {
    private val maxStreamsAmounr = 6

    fun createSoundPool(): SoundPool {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder()
                    .setMaxStreams(maxStreamsAmounr)
                    .build()
        } else  {
            SoundPool(maxStreamsAmounr, AudioManager.STREAM_MUSIC, 0)
        }
    }
}