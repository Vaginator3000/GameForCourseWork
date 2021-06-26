package com.template.game.drawers;

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.template.game.CELL_SIZE
import com.template.game.MAX_FIELD_HEIGHT
import com.template.game.MAX_FIELD_WIDTH
import com.template.game.R
import com.template.game.enums.Direction
import com.template.game.enums.Material
import com.template.game.models.Coordinate
import com.template.game.models.Element
import com.template.game.utils.getElementByCoord

class BulletDrawer(val container: FrameLayout) {
    var BULLET_WIDTH = 15
    var BULLET_HEIGHT = 25

    private var canBulletGoNext = true
    private var bulletThread: Thread? = null

    private fun checkBulletIsAlreadyExist() = bulletThread != null && bulletThread!!.isAlive

    fun moveBullet(view: View, currentDirection: Direction, elementsOnContainer: MutableList<Element>) {
        canBulletGoNext = true
        if(!checkBulletIsAlreadyExist()) {
            bulletThread = Thread {
                val bullet = createBullet(view, currentDirection)
                while (checkBulletCanMoveThrowBorder(bullet, Coordinate(bullet.top, bullet.left)) && canBulletGoNext) {
                    val lParams = bullet.layoutParams as FrameLayout.LayoutParams
                    when (currentDirection) {
                        Direction.UP -> lParams.topMargin -= BULLET_HEIGHT
                        Direction.BOTTOM -> lParams.topMargin += BULLET_HEIGHT
                        Direction.LEFT -> lParams.leftMargin -= BULLET_WIDTH
                        Direction.RIGHT -> lParams.leftMargin += BULLET_WIDTH
                    }
                    Thread.sleep(30)

                    bulletActionOnElements (
                            elementsOnContainer,
                            currentDirection,
                            Coordinate(bullet.top, bullet.left))

                    (container.context as Activity).runOnUiThread {
                        container.removeView(bullet)
                        container.addView(bullet,0)
                    }
                }
                (container.context as Activity).runOnUiThread {
                    container.removeView(bullet)
                }
            }
            bulletThread!!.start()
        }
    }

    private fun bulletActionOnElements(
                                elementsOnContainer: MutableList<Element>,
                                currentDirection: Direction,
                                bulletCoord: Coordinate) {

        compareElements(elementsOnContainer, getCoordsForBulletDirection(currentDirection, bulletCoord))
    }

    private fun compareElements(
            elementsOnContainer: MutableList<Element>,
            foundedCoordinates: List<Coordinate>) {

        foundedCoordinates.forEach {
            val elementToDelete = getElementByCoord(it, elementsOnContainer)
            removeElementsAndDeleteBullet(elementToDelete, elementsOnContainer)
        }

    }

    private fun removeElementsAndDeleteBullet(elementToDelete: Element?, elementsOnContainer: MutableList<Element>) {
        if(elementToDelete != null) {
            if (!elementToDelete.material.canBulletGoThrow)
                canBulletGoNext = false
            if (elementToDelete.material.canSmallBulletDestroy) {
                removeView(elementToDelete)
                elementsOnContainer.remove(elementToDelete)
            }
        }
    }

    private fun removeView(elementToDelete: Element) {
        val activity = container.context as Activity
        val viewToDelete = activity.findViewById<View>(elementToDelete.viewId)
        activity.runOnUiThread{
            container.removeView(viewToDelete)
        }
    }

    private fun getCoordsForBulletDirection(currentDirection: Direction, bulletCoord: Coordinate): List<Coordinate> {
        if (currentDirection == Direction.RIGHT || currentDirection == Direction.LEFT) {
            val topCell = bulletCoord.top - bulletCoord.top % CELL_SIZE
            val bottomCell = topCell + CELL_SIZE
            val leftCoord = bulletCoord.left - bulletCoord.left % CELL_SIZE
            return listOf(
                    Coordinate(topCell, leftCoord),
                    Coordinate(bottomCell, leftCoord) )
        }
        else {
            val leftCell = bulletCoord.left - bulletCoord.left % CELL_SIZE
            val rightCell = leftCell + CELL_SIZE
            val topCoord = bulletCoord.top - bulletCoord.top % CELL_SIZE
            return listOf(
                    Coordinate(topCoord, leftCell),
                    Coordinate(topCoord, rightCell) )
        }
    }

    private fun createBullet(veh: View, currentDirection: Direction)
        = ImageView(container.context)
                .apply {
                    this.setImageResource(R.drawable.bullet)
                    //--------------------------------------------------------------ПОМЕНЯТЬ ЗНАЧЕНИЯ РАЗМЕРА ПУЛИ ДЛЯ РАЗНЫХ ПУШЕК!!
                    this.layoutParams = FrameLayout.LayoutParams(BULLET_WIDTH,BULLET_HEIGHT)
                    val bulletCoord = getBulletCoord(this, veh, currentDirection)
                    (this.layoutParams as FrameLayout.LayoutParams).topMargin = bulletCoord.top
                    (this.layoutParams as FrameLayout.LayoutParams).leftMargin = bulletCoord.left
                    this.rotation = currentDirection.rotation

                }



    private fun checkBulletCanMoveThrowBorder(bullet: View, coord: Coordinate): Boolean {
        if ( coord.top >= 0
                && coord.left >= 0
                && coord.left + bullet.width <= MAX_FIELD_WIDTH
                && coord.top + bullet.height <= MAX_FIELD_HEIGHT)
                    return true

        return false
    }


    fun getBulletCoord(
            bullet: ImageView,
            veh: View,
            currentDirection: Direction
    ): Coordinate {
        val vehCoord = Coordinate(veh.top, veh.left)
        return when(currentDirection) {
            Direction.RIGHT -> {
                Coordinate (
                        top = getDistanceToMiddleOfVeh(vehCoord.top, bullet.layoutParams.height),
                        left = veh.left + veh.layoutParams.width)
            }
            Direction.LEFT -> {
                Coordinate (
                        top = getDistanceToMiddleOfVeh(veh.top, bullet.layoutParams.height),
                        left = veh.left - bullet.layoutParams.width)
            }
            Direction.UP -> {
                Coordinate (
                        top = veh.top - bullet.layoutParams.height,
                        left = getDistanceToMiddleOfVeh(veh.left, bullet.layoutParams.width))
            }
            Direction.BOTTOM -> {
                Coordinate (
                        top = veh.top + veh.layoutParams.height,
                        left = getDistanceToMiddleOfVeh(veh.left, bullet.layoutParams.width))
            }
        }
    }

    private fun getDistanceToMiddleOfVeh(vehCoord: Int, bulletSize: Int): Int
        = vehCoord + (CELL_SIZE - bulletSize / 2)
}
