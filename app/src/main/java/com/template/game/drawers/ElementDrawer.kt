package com.template.game.drawers

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.template.game.CELL_SIZE
import com.template.game.enums.Material
import com.template.game.models.Coordinate
import com.template.game.models.Element
import com.template.game.utils.getElementByCoord

class ElementDrawer(val container: FrameLayout) {
    var currentMaterial = Material.EMPTY
    var elements = mutableListOf<Element>()

    fun onTouchContainer(x: Float, y:Float) {
        val leftMargin = x.toInt() - (x.toInt() % CELL_SIZE)
        val topMargin = y.toInt() - (y.toInt() % CELL_SIZE)

        val coord = Coordinate(topMargin, leftMargin)
        if(currentMaterial == Material.EMPTY)
            delView(coord)
        else
            drawOrReplaceView(coord)
    }

    fun drawElemensOnStart (elements: List<Element>?) {
        elements?.forEach {
            currentMaterial = it.material
            drawView(it.coord)

        }
    }

    private fun drawOrReplaceView(coord: Coordinate) {
        val viewOnCoord = getElementByCoord(coord, elements)
        if (viewOnCoord == null)
            drawView(coord)
        else
            replaceView(coord)
    }

    private fun getElementsUnderCurrent(coord: Coordinate): List<Element> {
        val elementsToReturn = mutableListOf<Element>()
        elements.forEach {
            for (height in 0 until currentMaterial.height)
                for (width in 0 until currentMaterial.width) {
                    val bufCoord = Coordinate(
                        coord.top + height * CELL_SIZE,
                        coord.left + width * CELL_SIZE,
                    )
                    if (bufCoord == it.coord)
                        elementsToReturn.add(it)
                }
        }
        return elementsToReturn
    }

    private fun removeElement(element: Element?) {
        if (element != null) {
            val viewToDelete = container.findViewById<View>(element.viewId)
            container.removeView(viewToDelete)
            elements.remove(element)
        }
    }

    private fun replaceView(coord: Coordinate) {
        delView(coord)
        drawView(coord)
    }

    private fun delView(coord: Coordinate) {
        removeElement(getElementByCoord(coord,elements))
        getElementsUnderCurrent(coord).forEach {
            removeElement(it)
        }
    }

    private fun removeElementIfCanExistOnlyOne() {
        if (currentMaterial.amountElementsOnScreen != 0) {
            val thatElements = elements.filter { it.material == currentMaterial }
            if (thatElements.size >= currentMaterial.amountElementsOnScreen)
                elements.firstOrNull { it.coord == thatElements[0].coord }?.coord?.let { delView(it) }
        }

    }

    private fun drawView(coord: Coordinate) {
        removeElementIfCanExistOnlyOne()

        val view = ImageView(container.context)
        val lParams = FrameLayout.LayoutParams(currentMaterial.width * CELL_SIZE,
                                                currentMaterial.height * CELL_SIZE)

        view.setImageResource(currentMaterial.image!!)

        lParams.topMargin = coord.top
        lParams.leftMargin = coord.left

        val newElement = Element(
            material = currentMaterial,
            coord = coord,
            width = currentMaterial.width,
            height = currentMaterial.height
        )
        view.id = newElement.viewId
        view.layoutParams = lParams
        view.scaleType = ImageView.ScaleType.FIT_XY
        container.addView(view)
        elements.add(newElement)

    }
}
