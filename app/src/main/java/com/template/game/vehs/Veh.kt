package com.template.game.vehs

import android.view.View
import android.widget.FrameLayout
import com.template.game.*
import com.template.game.drawers.EnemyDrawer
import com.template.game.enums.Direction
import com.template.game.enums.Material
import com.template.game.models.Coordinate
import com.template.game.models.Element
import com.template.game.utils.*
import java.lang.Thread.sleep
import kotlin.random.Random


open class Veh(
        var container: FrameLayout,
        val element: Element,
        direction: Direction,
        elementsOnContainer: MutableList<Element>,
        private val enemyDrawer: EnemyDrawer) {

    var MOVE_VEH = false

    private val elements = elementsOnContainer
    var currentDirection = direction
    lateinit var vehView: View

    private fun init() {
        vehView = container.findViewById<View>(element.viewId) ?: return
            vehView.layoutParams.height = Material.ENEMY_VEH.height * CELL_SIZE
            vehView.layoutParams.width = Material.ENEMY_VEH.width * CELL_SIZE

            changeDirectionTo(currentDirection)
    }

    fun movePlayer() {
        init()

        Thread {
            while (true) {
                if (MOVE_VEH) {
                    if (!this.enemyDrawer.gameCore.isSeparating()) {
                        container.runOnUiThread {
                            changeVehPosition()
                        }
                    }
                    sleep(PLAYER_MOVE_PAUSE)
                }

            }
        }.start()
    }

    fun moveEnemy() {
        init()
        generateRandomDirection()
        container.runOnUiThread {
            changeVehPosition()
        }
    }

    fun isChanсeBiggerThanPercent(percent: Int): Boolean {
        return Random.nextInt(100) < percent
    }

    private fun generateRandomDirection() {
        if (isChanсeBiggerThanPercent(5))
            changeDirectionIfCantMove()

    }

    private fun changeDirectionIfCantMove() {
        changeDirectionTo(Direction.values()[Random.nextInt(Direction.values().size)])
    }

    fun changeDirectionTo(direction: Direction) {
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

        if (enemyDrawer.gameCore.isSeparating()) {
            if (element.material == Material.ENEMY_VEH && checkVehCanMoveThrowBorder() && element.checkVehCanMoveThrowElement(newCoord) ||
                    element.material == Material.PLAYER_VEH && checkPlayerWontBeKilledOnNextCoord(newCoord, currentCoord) && checkVehCanMoveThrowBorder() && element.checkVehCanMoveThrowElement(newCoord)    ) {
                viewMoving(container)
                element.coord = newCoord
            } else {
                changeDirectionIfCantMove()
                lParams.topMargin = currentCoord.top
                lParams.leftMargin = currentCoord.left
            }
        }
        else {
            if (checkVehCanMoveThrowBorder() && element.checkVehCanMoveThrowElement(newCoord)) {
                viewMoving(container)
                element.coord = newCoord
            } else {
                if (element.material == Material.ENEMY_VEH)
                    changeDirectionIfCantMove()
                lParams.topMargin = currentCoord.top
                lParams.leftMargin = currentCoord.left
            }

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

    private fun checkPlayerWontBeKilledOnNextCoord(nextCoord: Coordinate, currentCoord: Coordinate): Boolean {
        for (x in 0..MAX_FIELD_WIDTH) {
            val enemyElement = getVehByCoord(Coordinate(nextCoord.top, x), enemyDrawer.allEnemys)
            if (enemyElement != null) {
                if (enemyElement.coord == nextCoord || enemyElement.coord == currentCoord) continue
                val enemyVeh = getVehByElement(enemyElement)
                if (enemyVeh.element.coord.left > nextCoord.left && enemyVeh.currentDirection == Direction.LEFT)
                    return false
                if (enemyVeh.element.coord.left < nextCoord.left && enemyVeh.currentDirection == Direction.RIGHT)
                    return false
            }
        }
        for (y in 0..MAX_FIELD_HEIGHT) {
            val enemyElement = getVehByCoord(Coordinate(y, nextCoord.left), enemyDrawer.allEnemys)
            if (enemyElement != null) {
                if (enemyElement.coord == nextCoord || enemyElement.coord == currentCoord) continue
                val enemyVeh = getVehByElement(enemyElement)
                if (enemyVeh.element.coord.top > nextCoord.top && enemyVeh.currentDirection == Direction.UP)
                    return false
                if (enemyVeh.element.coord.top < nextCoord.top && enemyVeh.currentDirection == Direction.BOTTOM)
                    return false
            }
        }
        return true
    }

    private fun getVehByElement(enemyElement: Element): Veh {
        return enemyDrawer.allEnemys.firstOrNull { it.element == enemyElement }!!
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