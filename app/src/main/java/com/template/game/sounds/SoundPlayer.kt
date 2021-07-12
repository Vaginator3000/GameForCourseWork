package com.template.game.sounds

import android.content.Context
import android.media.MediaPlayer
import com.template.game.R

private const val INTRO_MUSIC_INDEX = 0
private const val BULLET_SHOT_INDEX = 1
private const val BULLET_BURST_INDEX = 2
private const val VEH_MOVE_INDEX = 3

class SoundPlayer(val context: Context) {

    private val sounds = mutableListOf<GameSound>()
    private val soundPool = SoundPoolFactory().createSoundPool()

    fun loadSounds() {
        sounds.add(INTRO_MUSIC_INDEX, GameSound(
                resourceInPool = soundPool.load(context, R.raw.intro, 1),
                pool = soundPool
        ))
        sounds.add(BULLET_SHOT_INDEX, GameSound(
                resourceInPool = soundPool.load(context, R.raw.bullet_shot, 1),
                pool = soundPool
        ))
        sounds.add(BULLET_BURST_INDEX, GameSound(
                resourceInPool = soundPool.load(context, R.raw.bullet_burst, 1),
                pool = soundPool
        ))
        sounds.add(VEH_MOVE_INDEX, GameSound(
                resourceInPool = soundPool.load(context, R.raw.veh_move, 1),
                pool = soundPool
        ))
    }

    fun playIntro() {
        sounds[INTRO_MUSIC_INDEX].startOrResume(false)
    }

    fun stopIntro() {
        stopSound(INTRO_MUSIC_INDEX)
    }

    fun bulletShot(){
        sounds[BULLET_SHOT_INDEX].play()
    }

    fun bulletBurst() {
        sounds[BULLET_BURST_INDEX].play()
    }

    fun vehMove() {
        sounds[VEH_MOVE_INDEX].startOrResume(true)
    }

    fun vehStop() {
        sounds[VEH_MOVE_INDEX].pause()
    }

    private fun pauseSound(i: Int) {
        sounds[i].pause()
    }

    fun pauseSounds() {
        for (i in 0 until sounds.size)
            pauseSound(i)
    }

    private fun stopSound(i: Int) {
        sounds[i].stop()
    }

    fun stopSounds() {
        for (i in 0 until sounds.size)
            stopSound(i)
    }

}