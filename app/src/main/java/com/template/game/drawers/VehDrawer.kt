package com.template.game.drawers

import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.template.game.CELL_SIZE
import com.template.game.MAX_FIELD_HEIGHT
import com.template.game.MAX_FIELD_WIDTH
import com.template.game.models.Coordinate
import com.template.game.models.Element
import com.template.game.utils.getElementByCoord
import kotlin.math.log

class VehDrawer(val container: FrameLayout) {
    fun changeVehPosition(veh: View, rightleft: Boolean, length: Int, elements: List<Element>) {
        val lParams = veh.layoutParams as FrameLayout.LayoutParams

        val currentCoord = Coordinate(lParams.topMargin, lParams.leftMargin)

        if (rightleft) lParams.leftMargin += length
        else lParams.topMargin += length

        val newCoord = Coordinate(lParams.topMargin, lParams.leftMargin)

        if (checkVehCanMoveThrowBorder(veh, rightleft, length)
                && checkVehCanMoveThrowElement(newCoord, elements)) {
            container.removeView(veh)
            container.addView(veh, 0)
        } else {
            lParams.topMargin = currentCoord.top
            lParams.leftMargin = currentCoord.left
        }


    }

    private fun checkVehCanMoveThrowBorder(veh: View, rightleft: Boolean, length: Int): Boolean {
        val lParams = veh.layoutParams as FrameLayout.LayoutParams

        if (rightleft && length == CELL_SIZE) // движение вправо
            if (lParams.leftMargin + veh.width < MAX_FIELD_WIDTH)
                return true

        if (rightleft && length == -CELL_SIZE) // движение влево
            if (lParams.leftMargin >= 0)
                return true

        if (!rightleft && length == CELL_SIZE) // движение вниз
            if (lParams.topMargin + veh.height < MAX_FIELD_HEIGHT)
                return true

        if (!rightleft && length == -CELL_SIZE) // движение вверх
            if (lParams.topMargin >= 0)
                return true

        return false
    }

    private fun checkVehCanMoveThrowElement(coord: Coordinate, elements: List<Element>): Boolean {
        getVehCoord(coord).forEach {
            val element = getElementByCoord(it, elements)
            if (element != null && !element.material.canVehGoThrow)
                return false
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