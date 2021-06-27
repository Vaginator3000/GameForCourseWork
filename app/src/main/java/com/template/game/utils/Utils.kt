package com.template.game.utils

import android.app.Activity
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.template.game.CELL_SIZE
import com.template.game.models.Coordinate
import com.template.game.models.Element


fun getElementByCoord(coord: Coordinate, elements: List<Element>): Element? {
    elements.forEach {
        for (height in 0 until it.height)
            for (width in 0 until it.width) {
                val searchingCoord = Coordinate(
                        it.coord.top + height * CELL_SIZE,
                        it.coord.left + width * CELL_SIZE,
                )
                if (searchingCoord == coord)
                    return it
            }
    }
    return null
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

    (container.context as Activity).runOnUiThread {
        container.addView(view)
    }
}

fun FrameLayout.runOnUiThread(block: () -> Unit) {
    (this.context as Activity).runOnUiThread {
        block()
    }
}