package com.template.game

import android.app.Activity
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import java.lang.Thread.sleep

class GameCore(val activity: Activity) {
    private var isPlay = false

    private var isPlayerDead = false

    fun isPlaying() = isPlay && !isPlayerDead

    fun startGame() {
        isPlay = true
    }

    fun pauseGame() {
        isPlay = false
    }

    fun killPlayer() {
        isPlay = false
        isPlayerDead = true
        animateEndGame()
    }

    private fun animateEndGame() {
        activity.runOnUiThread{
            val endGameText = activity.findViewById<TextView>(R.id.tvGameOver)
            endGameText.visibility = View.VISIBLE

            val slideUpAnim = AnimationUtils.loadAnimation(activity, R.anim.slide_up)
            endGameText.startAnimation(slideUpAnim)
        }

        Thread {
            sleep(4000)
            activity.runOnUiThread {
                activity.recreate()
            }
        }.start()
    }
}