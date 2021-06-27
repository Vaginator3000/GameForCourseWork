package com.template.game.drawers

import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import com.template.game.CELL_SIZE
import com.template.game.enums.Material
import com.template.game.models.Coordinate
import com.template.game.models.Element
import com.template.game.utils.drawElement

const val MAX_ENEMY_AMOUNT = 20

class EnemyDrawer(val container: FrameLayout) {
    private val spawnList = getSpawnList()
    private var currentCoord = spawnList[0]
    private var enemyAmount = 0

/*    init {
        spawnList = getSpawnList()
    }*/

    private fun getSpawnList(): List<Coordinate> {
        val spawnList = mutableListOf<Coordinate>()
        spawnList.add(Coordinate(0,0))
        spawnList.add(Coordinate(0,container.width / 2 - CELL_SIZE))
        spawnList.add(Coordinate(0,container.width - 2 * CELL_SIZE))
        return spawnList
    }

    fun startEnemyDrawing(elements: MutableList<Element>) {
        Thread {
            while (enemyAmount < MAX_ENEMY_AMOUNT) {
                drawEnemy(elements)
                enemyAmount++
                Thread.sleep(1000)
            }
        }.start()
    }

    private fun drawEnemy(elements: MutableList<Element>) {
        currentCoord = spawnList[enemyAmount % 3]

        val enemyElement = Element(
                material = Material.ENEMY_VEH,
                coord = currentCoord,
                width = CELL_SIZE,
                height = CELL_SIZE
        )
        enemyElement.drawElement(container)

        elements.add(enemyElement)
    }


}