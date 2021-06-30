package com.template.game.models

import android.view.View
import com.template.game.enums.Direction
import com.template.game.vehicals.Veh

data class Bullet(val view: View,
                  val direction: Direction,
                  val veh: Veh,
                  var canMove: Boolean = true) {
}