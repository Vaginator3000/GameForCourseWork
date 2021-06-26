package com.template.game

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.template.game.drawers.BulletDrawer
import com.template.game.drawers.ElementDrawer
import com.template.game.drawers.GridDrawer
import com.template.game.drawers.VehDrawer
import com.template.game.enums.Direction
import com.template.game.enums.Material
import com.template.game.models.Coordinate
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.util.*


const val CELL_SIZE = 50
var MOVE_VEH = false
var MAX_VERTICAL_CELLS_AMOUNT = 0
var MAX_HORIZONTAL_CELLS_AMOUNT = 0
var MAX_FIELD_HEIGHT = 0
var MAX_FIELD_WIDTH = 0

class MainActivity : AppCompatActivity() {
    private var editMode = false
    private var currentDirection = Direction.UP

    private val gridDrawer by lazy {
        GridDrawer(container)
    }

    private val elementDrawer by lazy {
        ElementDrawer(container)
    }

    private val bulletDrawer by lazy {
        BulletDrawer(container)
    }

    private val vehDrawer by lazy {
        VehDrawer(container)
    }

    private val lvlSaver by lazy {
        LvlSaver(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        touchListener()

        setMaxValues()

        elementDrawer.drawElemensOnStart(lvlSaver.loadLvl())

    }

    fun setMaxValues() {
        btnMenu.post {
            MAX_VERTICAL_CELLS_AMOUNT = mainLayout.height / CELL_SIZE
            MAX_HORIZONTAL_CELLS_AMOUNT = mainLayout.width / CELL_SIZE
            MAX_FIELD_HEIGHT = CELL_SIZE * MAX_VERTICAL_CELLS_AMOUNT
            MAX_FIELD_WIDTH = CELL_SIZE * MAX_HORIZONTAL_CELLS_AMOUNT
        }
    }


    fun touchListener() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                if (MOVE_VEH) {
                    val rightleft = (veh.rotation == 90f || veh.rotation == 270f)
                    val length = if (veh.rotation == 90f || veh.rotation == 180f) CELL_SIZE else -CELL_SIZE

                    runOnUiThread {
                        vehDrawer.changeVehPosition(veh, rightleft, length, elementDrawer.elements)
                    }

                    delay(100)
                }
            }
        }

        if (!editMode) {
            onTouchListenerNoInEditMode()

            container.removeView(veh)
            container.addView(veh)
        }

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
                veh.rotation = 270f
                currentDirection = Direction.LEFT
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                veh.rotation = 90f
                currentDirection = Direction.RIGHT
            }

            override fun onSwipeUp() {
                super.onSwipeUp()
                veh.rotation = 0f
                currentDirection = Direction.UP
            }

            override fun onSwipeDown() {
                super.onSwipeDown()
                veh.rotation = 180f
                currentDirection = Direction.BOTTOM
            }

            override fun onLongClick() {
                MOVE_VEH = true
                super.onLongClick()
            }

            override fun onUp() {
                MOVE_VEH = false
                super.onUp()
            }

            override fun onDoubleClick() {
                bulletDrawer.moveBullet(veh, currentDirection, elementDrawer.elements)
                super.onDoubleClick()
            }
        })
    }

    fun btnMenuOnClick(view: View) {
        showOrHideSettings(editMode)
        editMode = !editMode
    }

    private fun showOrHideSettings(show: Boolean) {
        if(show) {
            gridDrawer.removeGrid()
            btnMenu.background = resources.getDrawable(R.mipmap.ic_menu)
            materials_container.visibility = View.GONE
            game_btns_container.visibility = View.GONE
            onTouchListenerNoInEditMode()
        } else {
            gridDrawer.drawGrid()
            btnMenu.background = resources.getDrawable(R.drawable.ic_play)
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

        if(editMode) {
            onTouchListenerInEditMode()
        }
    }

    fun saveBtnOnClick(view: View) {
        lvlSaver.saveLvl(elementDrawer.elements)
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
    }

}

