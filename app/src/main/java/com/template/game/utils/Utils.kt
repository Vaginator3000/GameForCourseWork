package com.template.game.utils

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