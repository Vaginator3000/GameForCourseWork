package com.template.game.models

import android.view.View
import com.template.game.enums.Material

data class Element (
            val viewId: Int = View.generateViewId(),
            val material: Material,
            var coord: Coordinate,
            val width: Int = material.width,
            val height: Int = material.height) {
}