package com.template.game

import android.app.Activity
import android.graphics.Color
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import java.lang.Thread.sleep
import java.util.*

class GameCore(val activity: Activity) {
    private var isPlay = false
    private var isSeparate = false

    private var isPlayerDead = false

    fun isPlaying() = isPlay && !isPlayerDead

    fun isSeparating() = isSeparate

    fun starSeparate() {
        isPlay = true
        isSeparate = true
    }

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

    fun playerWon() {
        isPlay = false
        animateWinGame()
    }

    private fun animateEndGame() {
        activity.runOnUiThread{
            val endGameText = activity.findViewById<TextView>(R.id.tvEndGame)
            endGameText.text = activity.getText(R.string.lose_text)
            endGameText.setTextColor(Color.RED)
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

    private fun animateWinGame() {
        activity.runOnUiThread{
            val endGameText = activity.findViewById<TextView>(R.id.tvEndGame)
            endGameText.text = activity.getText(R.string.win_text)
            endGameText.setTextColor(Color.GREEN)
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