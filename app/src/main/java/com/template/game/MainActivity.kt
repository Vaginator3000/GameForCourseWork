package com.template.game

import android.graphics.Point
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.template.game.drawers.*
import com.template.game.enums.Direction
import com.template.game.enums.Material
import com.template.game.models.Coordinate
import com.template.game.models.Element
import com.template.game.sounds.SoundPlayer
import com.template.game.vehicals.Veh
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main_menu.*
import kotlinx.coroutines.*
import java.util.*


var CELL_SIZE = 0
var MAX_VERTICAL_CELLS_AMOUNT = 24
var MAX_HORIZONTAL_CELLS_AMOUNT = 14
var MAX_FIELD_HEIGHT = 0
var MAX_FIELD_WIDTH = 0

class MainActivity : AppCompatActivity() {
 //   private var editMode = false

    private val player by lazy {
        Veh(
                container,
                Element(
                        material = Material.PLAYER_VEH,
                        coord = getPlayerCoord()
                ),
                Direction.UP,
                elementDrawer.elements,
                enemyDrawer
        )
    }

    private val gridDrawer by lazy {
        GridDrawer(container)
    }

    private val elementDrawer by lazy {
        ElementDrawer(container)
    }

    private val bulletDrawer by lazy {
        BulletDrawer(container,elementDrawer.elements, enemyDrawer, soundPlayer, gameCore)
    }

    private val enemyDrawer by lazy {
        EnemyDrawer(container, elementDrawer.elements, soundPlayer, gameCore)
    }

    private val lvlSaver by lazy {
        LvlSaver(this)
    }

    private val gameCore by lazy {
        GameCore(this)
    }

    private val soundPlayer by lazy {
        SoundPlayer(this)
    }

    private fun getPlayerCoord() = Coordinate(
            top = MAX_FIELD_HEIGHT - Material.PLAYER_VEH.height * CELL_SIZE,
            left = MAX_FIELD_WIDTH - 8 * CELL_SIZE,
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setValues()
        fragmentBtnsListener()
        elementDrawer.drawElemensOnStart(lvlSaver.loadLvl())

        startGameProcess()

        val playerVehView = container.findViewById<View>(player.element.viewId)

        playerVehView.setOnClickListener {
            val lParams = playerVehView.layoutParams as FrameLayout.LayoutParams
            Toast.makeText(this,
                    "${playerVehView.width} --- ${playerVehView.height}\n${Material.PLAYER_VEH.width} --- ${Material.PLAYER_VEH.height}",
                    Toast.LENGTH_SHORT).show()
        }

    }

    override fun onPause() {
        super.onPause()
        pauseGame()
    }

    private fun startGameProcess() {
        soundPlayer.playIntro()

        elementDrawer.drawElemensOnStart(listOf(player.element))
        val playerVehView = container.findViewById<View>(player.element.viewId)

        playerVehView.layoutParams.width = Material.PLAYER_VEH.width * CELL_SIZE
        playerVehView.layoutParams.height = Material.PLAYER_VEH.height * CELL_SIZE

        elementDrawer.elements.add(player.element)
        player.movePlayer()
        enemyDrawer.startEnemyCreate()
    }

    private fun pauseGame() {
        gameCore.pauseGame()
        soundPlayer.pauseSounds()
        showOrHideSettings(gameCore.isPlaying())
    }

    private fun continueGame() {
        soundPlayer.stopIntro()
        gameCore.startGame()
        showOrHideSettings(gameCore.isPlaying())
    }

    fun fragmentBtnsListener() {
        btnStart.setOnClickListener {
            btnMenu.visibility = View.VISIBLE
            onTouchListenerNoInEditMode()
            menuFragment.view?.visibility = View.GONE
            startGameProcess()
            continueGame()
            enemyDrawer.startEnemyCreate()
        }
        btnSeparating.setOnClickListener {
            btnMenu.visibility = View.VISIBLE
            menuFragment.view?.visibility = View.GONE
        }
        btnRules.setOnClickListener {
        }
        btnExit.setOnClickListener {
            this.finish()
        }
    }

    fun setValues() {
        soundPlayer.loadSounds()

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width: Int = size.x
        val height: Int = size.y

        CELL_SIZE = width / MAX_HORIZONTAL_CELLS_AMOUNT
        MAX_FIELD_HEIGHT = CELL_SIZE * MAX_VERTICAL_CELLS_AMOUNT
        MAX_FIELD_WIDTH = CELL_SIZE * MAX_HORIZONTAL_CELLS_AMOUNT

        enemyDrawer.bulletDrawer = bulletDrawer
    }


    private fun onTouchListenerInEditMode() {
        container.setOnTouchListener { _, event ->
            elementDrawer.onTouchContainer(event.x, event.y)
            return@setOnTouchListener true
        }

    }

    private fun onTouchListenerNoInEditMode() {
        container.setOnTouchListener(object : OnSwipeListener(this@MainActivity) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                player.changeDirection(Direction.LEFT)
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                player.changeDirection(Direction.RIGHT)
            }

            override fun onSwipeUp() {
                super.onSwipeUp()
                player.changeDirection(Direction.UP)
            }

            override fun onSwipeDown() {
                super.onSwipeDown()
                player.changeDirection(Direction.BOTTOM)
            }

            override fun onLongClick() {
                player.MOVE_VEH = true
                soundPlayer.vehMove()
                super.onLongClick()
            }

            override fun onUp() {
                player.MOVE_VEH = false
                soundPlayer.vehStop()
                super.onUp()
            }

            override fun onDoubleClick() {
                bulletDrawer.addNewBulletForVeh(player)
                super.onDoubleClick()
            }
        })

    }

    fun btnMenuOnClick(view: View) {
        if(gameCore.isPlaying())
            pauseGame()
        else
            continueGame()
    }

    private fun showOrHideSettings(hide: Boolean) {
        if(hide) {
            gridDrawer.removeGrid()
            btnMenu.background = ContextCompat.getDrawable(this, R.mipmap.ic_menu)
            materials_container.visibility = View.GONE
            game_btns_container.visibility = View.GONE
            onTouchListenerNoInEditMode()
        } else {
            gridDrawer.drawGrid()
            btnMenu.background = ContextCompat.getDrawable(this, R.drawable.ic_play)
            materials_container.visibility = View.VISIBLE
            game_btns_container.visibility = View.VISIBLE
            onTouchListenerInEditMode()
        }

    }

    fun btnMaterialOnClick(view: View) {
        when(view.id) {
            R.id.btn_grass -> {
                elementDrawer.currentMaterial = Material.GRASS
            }
            R.id.btn_concrete -> {
                elementDrawer.currentMaterial = Material.CONCRETE
            }
            R.id.btn_brick -> {
                elementDrawer.currentMaterial = Material.BRICK
            }
            R.id.btn_base -> {
                elementDrawer.currentMaterial = Material.BASE
            }
            R.id.btn_clear -> {
                elementDrawer.currentMaterial = Material.EMPTY
            }
        }

        if(!gameCore.isPlaying()) {
            onTouchListenerInEditMode()
        }
    }

    fun saveBtnOnClick(view: View) {
        lvlSaver.saveLvl(elementDrawer.elements)
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPlayer.stopSounds()
    }

}

