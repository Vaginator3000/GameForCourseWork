package com.template.game.sounds

import android.media.SoundPool
import android.util.Log

class GameSound(
    var resourceInPool: Int,
    var isStarted: Boolean = false,
    val pool: SoundPool
) {
    fun play() {
        pool.play(resourceInPool, 1f, 1f, 1, 0, 1f)
    }

    fun startOrResume(isLooping: Boolean) {
        val isLoop = if (isLooping) -1 else 0

        if(isStarted)
            pool.resume(resourceInPool)
        else {
            isStarted = true
            resourceInPool = pool.play(resourceInPool, 1f, 1f, 1, isLoop, 1f)
            Log.d("MyLog", pool.toString())
            Log.d("MyLog", resourceInPool.toString())
        }
    }

    fun pause() {
        pool.pause(resourceInPool)
    }

    fun stop() {
        pool.stop(resourceInPool)
    }

}