package com.template.game.vehicals

import android.view.View
import android.widget.FrameLayout
import com.template.game.CELL_SIZE
import com.template.game.MAX_FIELD_HEIGHT
import com.template.game.MAX_FIELD_WIDTH
import com.template.game.drawers.BulletDrawer
import com.template.game.drawers.EnemyDrawer
import com.template.game.enums.Direction
import com.template.game.enums.Material
import com.template.game.models.Coordinate
import com.template.game.models.Element
import com.template.game.utils.*
import java.lang.Thread.sleep
import kotlin.random.Random


class Veh(
        var container: FrameLayout,
        val element: Element,
        direction: Direction,
        elementsOnContainer: MutableList<Element>,
        val enemyDrawer: EnemyDrawer) {

    var MOVE_VEH = false
    val elements = elementsOnContainer
    var currentDirection = direction
    lateinit var vehView: View

    private fun init() {
        vehView = container.findViewById<View>(element.viewId) ?: return
        if (vehView != null && element.material == Material.PLAYER_VEH || element.material == Material.ENEMY_VEH) {
            vehView.layoutParams.height = Material.ENEMY_VEH.height * CELL_SIZE
            vehView.layoutParams.width = Material.ENEMY_VEH.width * CELL_SIZE

            changeDirection(currentDirection)
        }
    }

    fun movePlayer() {
        init()

        Thread {
            while (true) {
                if (MOVE_VEH) {
                    container.runOnUiThread {
                        changeVehPosition()
                    }

                    sleep(100)

                }

            }
        }.start()
    }

    fun moveEnemy() {
        init()
        generateRandomDirectionForEnemy()
        container.runOnUiThread {
            changeVehPosition()
        }
    }

    fun isChanсeBiggerThanPercent(percent: Int): Boolean {
        val random = Random.nextInt(100)
        return random <= percent
    }

    private fun generateRandomDirectionForEnemy() {
        if (element.material == Material.ENEMY_VEH) {
            if (isChanсeBiggerThanPercent(5))
                changeEnemyDirectionIfCantMove()
        }
    }

    private fun changeEnemyDirectionIfCantMove() {
        changeDirection(Direction.values()[Random.nextInt(Direction.values().size)])
    }

    fun changeDirection(direction: Direction) {
        container.runOnUiThread {
            when (direction) {
                Direction.UP -> vehView.rotation = 0f
                Direction.BOTTOM -> vehView.rotation = 180f
                Direction.RIGHT -> vehView.rotation = 90f
                Direction.LEFT -> vehView.rotation = 270f
            }
        }
        currentDirection = direction
    }

    private fun changeVehPosition() {
        if (element.material == Material.PLAYER_VEH)
            vehView = container.findViewById<View>(element.viewId)
        val lParams = vehView.layoutParams as FrameLayout.LayoutParams

        val currentCoord = vehView.getViewCurrentCoord()
        val newCoord = getNextVehCoord()

        if (checkVehCanMoveThrowBorder() && element.checkVehCanMoveThrowElement(newCoord)) {
                viewMoving(container)
                element.coord = newCoord
        } else {
            if (element.material == Material.ENEMY_VEH)
                changeEnemyDirectionIfCantMove()
            lParams.topMargin = currentCoord.top
            lParams.leftMargin = currentCoord.left
        }
    }

    private fun viewMoving(container: FrameLayout) {
        container.runOnUiThread {
            container.removeView(vehView)
            container.addView(vehView, 0)
        }
    }


    private fun getNextVehCoord(): Coordinate {
        val rightleft = (currentDirection == Direction.RIGHT || currentDirection == Direction.LEFT)
        val length = if (currentDirection == Direction.BOTTOM || currentDirection == Direction.RIGHT) CELL_SIZE else -CELL_SIZE

        val lParams = vehView.layoutParams as FrameLayout.LayoutParams

        if (rightleft)
            lParams.leftMargin += length
        else
            lParams.topMargin += length

        return Coordinate(lParams.topMargin, lParams.leftMargin)
    }

    private fun checkVehCanMoveThrowBorder(): Boolean {
        val rightleft = (currentDirection == Direction.RIGHT || currentDirection == Direction.LEFT)
        val length = if (currentDirection == Direction.BOTTOM || currentDirection == Direction.RIGHT) CELL_SIZE else -CELL_SIZE

        val lParams = vehView.layoutParams as FrameLayout.LayoutParams


        if (rightleft && length == CELL_SIZE) // движение вправо
            if (lParams.leftMargin + vehView.layoutParams.width <= MAX_FIELD_WIDTH)
                return true

        if (rightleft && length == -CELL_SIZE) // движение влево
            if (lParams.leftMargin >= 0)
                return true

        if (!rightleft && length == CELL_SIZE) // движение вниз
            if (lParams.topMargin + vehView.layoutParams.height <= MAX_FIELD_HEIGHT)
                return true

        if (!rightleft && length == -CELL_SIZE) // движение вверх
            if (lParams.topMargin >= 0)
                return true

        return false
    }

    private fun Element.checkVehCanMoveThrowElement(
        coordinate: Coordinate
    ): Boolean {
        for (anyCoord in getVehCoord(coordinate)) {
            var element = getElementByCoord(anyCoord, elements)
            if (element == null) {
                element = getVehByCoord(anyCoord, enemyDrawer.allEnemys)
            }
            if (element != null && !element.material.canVehGoThrow) {
                if (this == element) continue
                return false
            }
        }
        return true
    }

    private fun getVehCoord(topLeftCoord: Coordinate): List<Coordinate> {
        val coordList = mutableListOf<Coordinate>()
        coordList.add(topLeftCoord) //top left
        coordList.add(Coordinate(topLeftCoord.top + CELL_SIZE, topLeftCoord.left)) // bottom left
        coordList.add(Coordinate(topLeftCoord.top, topLeftCoord.left + CELL_SIZE)) // top right
        coordList.add(Coordinate(topLeftCoord.top + CELL_SIZE, topLeftCoord.left + CELL_SIZE)) //bottom right

        return coordList
    }

}