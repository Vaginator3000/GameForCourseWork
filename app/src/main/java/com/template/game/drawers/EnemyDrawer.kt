package com.template.game.drawers

import android.widget.FrameLayout
import com.template.game.CELL_SIZE
import com.template.game.GameCore
import com.template.game.MAX_FIELD_WIDTH
import com.template.game.sounds.SoundPlayer
import com.template.game.enums.Direction
import com.template.game.enums.Material
import com.template.game.models.Coordinate
import com.template.game.models.Element
import com.template.game.utils.drawElement
import com.template.game.vehicals.Veh

const val MAX_ENEMY_AMOUNT = 5

class EnemyDrawer(
        val container: FrameLayout,
        val elements: MutableList<Element>,
        val soundPlayer: SoundPlayer,
        val gameCore: GameCore) {

    private val spawnList: List<Coordinate>
    val allEnemys = mutableListOf<Veh>()
    private var currentCoord: Coordinate
    private var enemyAmount = 0
    private var gameStarted = false

    lateinit var bulletDrawer: BulletDrawer

    init {
        spawnList = getSpawnList()
        currentCoord = spawnList[0]
    }

    private fun getSpawnList(): List<Coordinate> {
        val spawnList = mutableListOf<Coordinate>()
        spawnList.add(Coordinate(0, 0))
        spawnList.add(Coordinate(0, MAX_FIELD_WIDTH / 2 - CELL_SIZE))
        spawnList.add(Coordinate(0, MAX_FIELD_WIDTH - 2 * CELL_SIZE))
        return spawnList
    }

    fun startEnemyCreate() {
        if(gameStarted) {
            return
        }
        gameStarted = true
        Thread {
            while (enemyAmount < MAX_ENEMY_AMOUNT) {
                if (!gameCore.isPlaying()) continue
                drawEnemy()
                enemyAmount++
                Thread.sleep(5000)
            }
        }.start()
        moveEnemyVeh()
    }

    private fun drawEnemy() {
        currentCoord = spawnList[enemyAmount % 3]

        val enemyElement = Element(
                material = Material.ENEMY_VEH,
                coord = currentCoord
        )
        enemyElement.drawElement(container)

        //    while ( container.findViewById<View>(enemyElement.viewId) == null ) {}
        val enemyVeh = Veh(
                container,
                enemyElement,
                Direction.BOTTOM,
                elements,
                this
        )
        allEnemys.add(enemyVeh)
    }

    private fun moveEnemyVeh() {
        Thread {
            while (true) {
                if (!gameCore.isPlaying()) continue
                Thread.sleep(200)
                goThrowAllVehs()
            }
        }.start()
    }

    private fun goThrowAllVehs() {
        if(allEnemys.isNotEmpty())
            soundPlayer.vehMove()
        else
            soundPlayer.vehStop()
        allEnemys.toList().forEach {
            it.moveEnemy()
            if (isEnemySeePlayer(it) || it.isChanсeBiggerThanPercent(10))
                bulletDrawer.addNewBulletForVeh(it)
        }
    }

    private fun isEnemySeePlayer(enemy: Veh): Boolean {
        val playerElement = elements.firstOrNull { it.material == Material.PLAYER_VEH}

        if (playerElement != null) {
            when (enemy.currentDirection) {
                Direction.UP -> {
                    if (playerElement.coord.top < enemy.element.coord.top &&
                            playerElement.coord.left == enemy.element.coord.left)
                        return true
                }
                Direction.BOTTOM -> {
                    if (playerElement.coord.top > enemy.element.coord.top &&
                            playerElement.coord.left == enemy.element.coord.left)
                        return true
                }
                Direction.LEFT -> {
                    if (playerElement.coord.top == enemy.element.coord.top &&
                            playerElement.coord.left < enemy.element.coord.left)
                        return true
                }
                Direction.RIGHT -> {
                    if (playerElement.coord.top == enemy.element.coord.top &&
                            playerElement.coord.left > enemy.element.coord.left)
                        return true
                }
            }
        }
        return false
    }

    fun removeVeh(vehIndex: Int) {
        if (vehIndex < 0) return
        soundPlayer.bulletBurst()
        allEnemys.removeAt(vehIndex)
    }
}