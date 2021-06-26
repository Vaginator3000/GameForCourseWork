package com.template.game.drawers

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.template.game.CELL_SIZE
import com.template.game.MAX_FIELD_HEIGHT
import com.template.game.MAX_FIELD_WIDTH
import com.template.game.R

class GridDrawer(val container: FrameLayout) {
    private val lines = ArrayList<View>()

    fun drawGrid() {
        drawVerticalLines()
        drawHorizontalLines()
    }

    fun removeGrid() {
        lines.forEach {
            container.removeView(it)
        }
    }

    private fun drawVerticalLines() {
        var lMargin = 0
        while (lMargin < container.width) {
            lMargin += CELL_SIZE

            val verticalLine = View(container.context)
            verticalLine.layoutParams = FrameLayout.LayoutParams(1, MAX_FIELD_HEIGHT)
            (verticalLine.layoutParams as FrameLayout.LayoutParams).leftMargin = lMargin
            verticalLine.setBackgroundColor(container.context.resources.getColor(R.color.white))
            lines.add(verticalLine)

            container.addView(verticalLine)
        }
    }

    private fun drawHorizontalLines() {
        var tMargin = 0
        while (tMargin < container.height) {
            tMargin += CELL_SIZE

            val horizontalLine = View(container.context)
            horizontalLine.layoutParams = FrameLayout.LayoutParams(MAX_FIELD_WIDTH, 1)
            (horizontalLine.layoutParams as FrameLayout.LayoutParams).topMargin = tMargin
            horizontalLine.setBackgroundColor(container.context.resources.getColor(R.color.white))
            lines.add(horizontalLine)

            container.addView(horizontalLine)
        }
    }
}