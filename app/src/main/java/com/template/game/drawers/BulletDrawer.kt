package com.template.game.drawers;

import android.app.Activity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.template.game.*
import com.template.game.enums.Direction
import com.template.game.enums.Material
import com.template.game.models.Bullet
import com.template.game.models.Coordinate
import com.template.game.models.Element
import com.template.game.sounds.SoundPlayer
import com.template.game.utils.*
import com.template.game.vehs.Veh
import java.lang.Thread.sleep
import kotlin.math.abs

class BulletDrawer (
        val container: FrameLayout,
        val elements: MutableList<Element>,
        val enemyDrawer: EnemyDrawer,
        val soundPlayer: SoundPlayer,
        val gameCore: GameCore) {

    init {
        movaAllBullets()
    }

    private var BULLET_WIDTH = 15
    private var BULLET_HEIGHT = 25

    private val elementsOnContainer = elements
    private val allBullets = mutableListOf<Bullet>()

    private fun movaAllBullets() {
        Thread {
            while (true) {
                if (!gameCore.isPlaying()) continue
                interactWithBullets()
                sleep(30)
            }
        }.start()
    }

    private fun interactWithBullets() {
        allBullets.toList().forEach { bullet ->
            val bulletView = bullet.view
            val lParams = bulletView.layoutParams as FrameLayout.LayoutParams
            if (bullet.canBulletMoveNext()) {
                when (bullet.direction) {
                    Direction.UP -> lParams.topMargin -= BULLET_HEIGHT
                    Direction.BOTTOM -> lParams.topMargin += BULLET_HEIGHT
                    Direction.LEFT -> lParams.leftMargin -= BULLET_WIDTH
                    Direction.RIGHT -> lParams.leftMargin += BULLET_WIDTH
                }

                bulletActionOnElements(bullet)

                container.runOnUiThread {
                    container.removeView(bulletView)
                    container.addView(bulletView)
                }
            } else {
                stopBullet(bullet)
            }
            bullet.stopIntersectingBullets()
        }
        removeInconsistentBullets()
    }

    private fun removeInconsistentBullets() {
        val removingList = allBullets.filter { !it.canMove }
        removingList.forEach {
            stopBullet(it)
            container.runOnUiThread {
                container.removeView(it.view)
            }
        }
        allBullets.removeAll(removingList)

    }

    private fun Bullet.stopIntersectingBullets() {
        val thisBulletCoord = this.view.getViewCurrentCoord()
        for (bullet in allBullets) {
            if (bullet == this)
                continue

            val bulletCoord = bullet.view.getViewCurrentCoord()

            if (abs(bulletCoord.left - thisBulletCoord.left) < CELL_SIZE / 2 &&
                    abs(bulletCoord.top - thisBulletCoord.top) < CELL_SIZE / 2) {


                stopBullet(bullet)
                stopBullet(this)
                return
            }
        }
    }

    fun addNewBulletForVeh(veh: Veh) {
        container.findViewById<View>(veh.element.viewId) ?: return

        soundPlayer.bulletShot()

        if (veh.hasBullet()) return
        allBullets.add(Bullet(createBullet(veh), veh.currentDirection, veh))
    }

    private fun Veh.hasBullet(): Boolean =
            allBullets.firstOrNull { it.veh == this } != null

    private fun Bullet.canBulletMoveNext() = checkBulletCanMoveThrowBorder(this.view, this.view.getViewCurrentCoord()) && this.canMove


    private fun bulletActionOnElements(
            bullet: Bullet) {

        compareElements(getCoordsForBulletDirection(bullet), bullet)
    }

    private fun compareElements(
            foundedCoordinates: List<Coordinate>,
            bullet: Bullet) {

        foundedCoordinates.forEach {
            var elementToDelete = getElementByCoord(it, elementsOnContainer)
            if (elementToDelete == null)
                elementToDelete = getVehByCoord(it, enemyDrawer.allEnemys)
            if (elementToDelete != bullet.veh.element)
                removeElementsAndDeleteBullet(elementToDelete, bullet)
        }

    }

    private fun removeElementsAndDeleteBullet(elementToDelete: Element?, bullet: Bullet) {
        if (elementToDelete != null) {
            if (!elementToDelete.material.canBulletGoThrow)
                stopBullet(bullet)
            if (bullet.veh.element.material == Material.ENEMY_VEH && elementToDelete.material == Material.ENEMY_VEH) {
                stopBullet(bullet)
                return
            }
            if (elementToDelete.material.canSmallBulletDestroy) {
                removeView(elementToDelete)
                elementsOnContainer.remove(elementToDelete)
                removeVeh(elementToDelete)
            }
        }
    }

    private fun removeVeh(element: Element) {
        if (element.material == Material.PLAYER_VEH)
            gameCore.killPlayer()
        val vehElements = enemyDrawer.allEnemys.map { it.element }
        val vehIndex = vehElements.indexOf(element)
        enemyDrawer.removeVeh(vehIndex)
    }

    private fun stopBullet(bullet: Bullet) {
        bullet.canMove = false
    }

    private fun removeView(elementToDelete: Element) {
        val activity = container.context as Activity
        val viewToDelete = activity.findViewById<View>(elementToDelete.viewId)
        activity.runOnUiThread {
            container.removeView(viewToDelete)
        }
    }

    private fun getCoordsForBulletDirection(bullet: Bullet): List<Coordinate> {
        val currentDirection = bullet.direction
        val bulletCoord = bullet.view.getViewCurrentCoord()

        if (currentDirection == Direction.RIGHT || currentDirection == Direction.LEFT) {
            val leftCoord = bulletCoord.left - bulletCoord.left % CELL_SIZE
            val topCoord = bulletCoord.top - bulletCoord.top % CELL_SIZE
            var bottomCoord = topCoord + CELL_SIZE
            if (bullet.veh.element.material.width == 1)
                bottomCoord = topCoord
            return listOf(
                    Coordinate(topCoord, leftCoord),
                    Coordinate(bottomCoord, leftCoord))
        } else {
            val topCoord = bulletCoord.top - bulletCoord.top % CELL_SIZE
            val leftCoord = bulletCoord.left - bulletCoord.left % CELL_SIZE
            var rightCoord = leftCoord + CELL_SIZE
            if (bullet.veh.element.material.width == 1)
                rightCoord = leftCoord
            return listOf(
                    Coordinate(topCoord, leftCoord),
                    Coordinate(topCoord, rightCoord))
        }
    }

    private fun createBullet(veh: Veh): ImageView {
        val currentDirection = veh.currentDirection
        return ImageView(container.context)
            .apply {
                            this.setImageResource(R.drawable.bullet)
                            //--------------------------------------------------------------ПОМЕНЯТЬ ЗНАЧЕНИЯ РАЗМЕРА ПУЛИ ДЛЯ РАЗНЫХ ПУШЕК!!
                            this.layoutParams = FrameLayout.LayoutParams(BULLET_WIDTH, BULLET_HEIGHT)
                            val bulletCoord = getBulletCoord(this, veh)
                            (this.layoutParams as FrameLayout.LayoutParams).topMargin = bulletCoord.top
                            (this.layoutParams as FrameLayout.LayoutParams).leftMargin = bulletCoord.left
                            this.rotation = currentDirection.rotation

            }
    }


    private fun checkBulletCanMoveThrowBorder(bullet: View, coord: Coordinate): Boolean {
        if ( coord.top >= 0
                && coord.left >= 0
                && coord.left + bullet.width <= MAX_FIELD_WIDTH
                && coord.top + bullet.height <= MAX_FIELD_HEIGHT)
                    return true

        return false
    }


    private fun getBulletCoord(
            bullet: ImageView,
            veh: Veh
    ): Coordinate {
        val vehView = container.findViewById<View>(veh.element.viewId)
        val currentDirection = veh.currentDirection
        val vehCoord = Coordinate(vehView.top, vehView.left)
        return when(currentDirection) {
            Direction.RIGHT -> {
                Coordinate (
                        top = getDistanceToMiddleOfVeh(veh, vehCoord.top, bullet.layoutParams.height),
                        left = vehView.left + vehView.layoutParams.width)
            }
            Direction.LEFT -> {
                Coordinate (
                        top = getDistanceToMiddleOfVeh(veh, vehView.top, bullet.layoutParams.height),
                        left = vehView.left - bullet.layoutParams.width)
            }
            Direction.UP -> {
                Coordinate (
                        top = vehView.top - bullet.layoutParams.height,
                        left = getDistanceToMiddleOfVeh(veh, vehView.left, bullet.layoutParams.width))
            }
            Direction.BOTTOM -> {
                Coordinate (
                        top = vehView.top + vehView.layoutParams.height,
                        left = getDistanceToMiddleOfVeh(veh, vehView.left, bullet.layoutParams.width))
            }
        }
    }

    private fun getDistanceToMiddleOfVeh(veh: Veh, vehCoord: Int, bulletSize: Int): Int {
        val indent = if (veh.element.width == 1) CELL_SIZE / 2 else CELL_SIZE
        return vehCoord + (indent - bulletSize / 2)
    }
}
