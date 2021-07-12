package com.template.game.drawers

import android.widget.FrameLayout
import com.template.game.*
import com.template.game.sounds.SoundPlayer
import com.template.game.enums.Direction
import com.template.game.enums.Material
import com.template.game.models.Coordinate
import com.template.game.models.Element
import com.template.game.utils.drawElement
import com.template.game.vehs.BigTank
import com.template.game.vehs.OldTank
import com.template.game.vehs.RealTank
import com.template.game.vehs.Veh
import kotlin.math.abs
import kotlin.random.Random

class EnemyDrawer(
        val container: FrameLayout,
        val elements: MutableList<Element>,
        val soundPlayer: SoundPlayer,
        val gameCore: GameCore) {

    private val spawnList: List<Coordinate>
    val allEnemys = mutableListOf<Veh>()
    private var currentCoord: Coordinate
    private var enemyAmount = 0
    private var deadEnemyAmount = 0
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
                if (enemyAmount % 3 == 0)
                    Thread.sleep(10000)
                else
                    Thread.sleep(5000)
            }
        }.start()
        moveEnemyVeh()
    }

    fun makePlayerVehAlive(player: Veh) {
        allEnemys.add(player)
    }

    private fun drawEnemy() {
        currentCoord = spawnList[enemyAmount % 3]

        val enemyElement = Element(
                material = Material.ENEMY_VEH,
                coord = currentCoord
        )
        enemyElement.drawElement(container)

        val enemyVeh = getRandEnemy(enemyElement)

        allEnemys.add(enemyVeh)
    }

    private fun getRandEnemy(enemyElement: Element): Veh {
        lateinit var enemyVeh: Veh

        val rand = Random.nextInt(100)
        if (rand % 3 == 0) {
            enemyVeh = OldTank(
                    container,
                    enemyElement,
                    Direction.BOTTOM,
                    elements,
                    this
            )
        }
        if (rand % 3 == 1) {
            enemyVeh = RealTank(
                    container,
                    enemyElement,
                    Direction.BOTTOM,
                    elements,
                    this
            )
        }
        if (rand % 3 == 2) {
            enemyVeh = BigTank(
                    container,
                    enemyElement,
                    Direction.BOTTOM,
                    elements,
                    this
            )
        }
        return enemyVeh
    }

    private fun moveEnemyVeh() {
        Thread {
            while (true) {
                if (!gameCore.isPlaying()) continue
                Thread.sleep(PLAYER_MOVE_PAUSE + 100)
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
            if (it.element.material == Material.PLAYER_VEH) {
                if (isPlayerSeeEnemy(it) || it.isChanсeBiggerThanPercent(10))
                    bulletDrawer.addNewBulletForVeh(it)
            } else {
                if (isEnemySeePlayer(it) || it.isChanсeBiggerThanPercent(10))
                    bulletDrawer.addNewBulletForVeh(it)
            }
        }
    }

    private fun isEnemySeePlayer(enemy: Veh): Boolean {
        val playerElement = elements.firstOrNull { it.material == Material.PLAYER_VEH}

        if (playerElement != null) {
            when (enemy.currentDirection) {
                Direction.UP -> {
                    if (playerElement.coord.top < enemy.element.coord.top &&
                            abs(playerElement.coord.left - enemy.element.coord.left) < CELL_SIZE * 2)
                        return true
                }
                Direction.BOTTOM -> {
                    if (playerElement.coord.top > enemy.element.coord.top &&
                            abs(playerElement.coord.left - enemy.element.coord.left) < CELL_SIZE * 2)
                        return true
                }
                Direction.LEFT -> {
                    if (abs(playerElement.coord.top - enemy.element.coord.top) < CELL_SIZE * 2 &&
                            playerElement.coord.left < enemy.element.coord.left)
                        return true
                }
                Direction.RIGHT -> {
                    if (abs(playerElement.coord.top - enemy.element.coord.top) < CELL_SIZE * 2 &&
                            playerElement.coord.left > enemy.element.coord.left)
                        return true
                }
            }
        }
        return false
    }

    private fun isPlayerSeeEnemy(player: Veh): Boolean {
        for (enemyElement in allEnemys.map { it.element }.filter { it.material == Material.ENEMY_VEH }) {

            when (player.currentDirection) {
                Direction.UP -> {
                    if (enemyElement.coord.top < player.element.coord.top &&
                            abs(enemyElement.coord.left - player.element.coord.left) < CELL_SIZE * 2)
                        return true
                }
                Direction.BOTTOM -> {
                    if (enemyElement.coord.top > player.element.coord.top &&
                            abs(enemyElement.coord.left - player.element.coord.left) < CELL_SIZE * 2)
                        return true
                }
                Direction.LEFT -> {
                    if (abs(enemyElement.coord.top - player.element.coord.top) < CELL_SIZE * 2 &&
                            enemyElement.coord.left < player.element.coord.left)
                        return true
                }
                Direction.RIGHT -> {
                    if (abs(enemyElement.coord.top - player.element.coord.top) < CELL_SIZE * 2 &&
                            enemyElement.coord.left > player.element.coord.left)
                        return true
                }
            }
        }
        return false
    }

    fun removeVeh(vehIndex: Int) {
        if (vehIndex < 0) return
        soundPlayer.bulletBurst()

        if (allEnemys[vehIndex].element.material == Material.ENEMY_VEH) {
            deadEnemyAmount++
            if (deadEnemyAmount == MAX_ENEMY_AMOUNT)
                gameCore.playerWon()
        }
        allEnemys.removeAt(vehIndex)
    }
}