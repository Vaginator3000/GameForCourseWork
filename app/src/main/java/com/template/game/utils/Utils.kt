package com.template.game.utils

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.template.game.CELL_SIZE
import com.template.game.models.Coordinate
import com.template.game.models.Element
import com.template.game.vehicals.Veh


fun getElementByCoord(coord: Coordinate, elements: List<Element>): Element? {
    for(element in elements.toList()) {
        for (height in 0 until element.height)
            for (width in 0 until element.width) {
                val searchingCoord = Coordinate(
                        element.coord.top + height * CELL_SIZE,
                        element.coord.left + width * CELL_SIZE,
                )
                if (searchingCoord == coord)
                    return element
            }
    }
    return null
}

fun View.getViewCurrentCoord(): Coordinate {
    val lParams = this.layoutParams as FrameLayout.LayoutParams
    return Coordinate(lParams.topMargin, lParams.leftMargin)
}

fun getVehByCoord(coord: Coordinate, enemyVehs: MutableList<Veh>): Element? {
    return getElementByCoord(coord, enemyVehs.map { it.element })
}

fun Element.drawElement(container: FrameLayout) {
    val view = ImageView(container.context)
    val lParams = FrameLayout.LayoutParams(this.material.width * CELL_SIZE,
            this.material.height * CELL_SIZE)

    view.setImageResource(this.material.image!!)

    lParams.topMargin = coord.top
    lParams.leftMargin = coord.left

    view.id = this.viewId
    view.layoutParams = lParams
    view.scaleType = ImageView.ScaleType.FIT_XY

    container.runOnUiThread {
        container.addView(view)
    }
}

fun FrameLayout.runOnUiThread(block: () -> Unit) {
    (this.context as Activity).runOnUiThread {
        block()
    }
}