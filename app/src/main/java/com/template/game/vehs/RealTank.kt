package com.template.game.vehs

import android.widget.FrameLayout
import com.template.game.R
import com.template.game.drawers.EnemyDrawer
import com.template.game.enums.Direction
import com.template.game.models.Element

class RealTank(
        container: FrameLayout,
        element: Element,
        direction: Direction,
        elementsOnContainer: MutableList<Element>,
        enemyDrawer: EnemyDrawer) : Veh(container, element, direction,elementsOnContainer,enemyDrawer) {
    init {
        element.material.image = R.drawable.real_tank
    }
}