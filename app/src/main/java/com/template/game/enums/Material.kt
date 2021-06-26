package com.template.game.enums

import com.template.game.R

var SPAWN_SIZE = 2
var ELEMENT_SIZE = 1
var BASE_WIDTH = 4
var BASE_HEIGHT = 3


enum class Material(val canVehGoThrow: Boolean,
                    val canBulletGoThrow: Boolean,
                    val canSmallBulletDestroy: Boolean,
                    internal val amountElementsOnScreen: Int,
                    val width: Int,
                    val height: Int,
                    val image: Int?) {

    EMPTY (
        canVehGoThrow = true,
        canBulletGoThrow = true,
        canSmallBulletDestroy = true,
        amountElementsOnScreen = 0,
        width = 0,
        height = 0,
        image = null
    ),

    GRASS (
        canVehGoThrow = true,
        canBulletGoThrow = true,
        canSmallBulletDestroy = false,
        amountElementsOnScreen = 0,
        width = ELEMENT_SIZE,
        height = ELEMENT_SIZE,
        image = R.drawable.grass
    ),

    CONCRETE (
        canVehGoThrow = false,
        canBulletGoThrow = false,
        canSmallBulletDestroy = false,
        amountElementsOnScreen = 0,
        width = ELEMENT_SIZE,
        height = ELEMENT_SIZE,
        image = R.drawable.concrete
    ),

    BRICK (
        canVehGoThrow = false,
        canBulletGoThrow = false,
        canSmallBulletDestroy = true,
        amountElementsOnScreen = 0,
        width = ELEMENT_SIZE,
        height = ELEMENT_SIZE,
        image = R.drawable.brick
    ),

    BASE (
        canVehGoThrow = false,
        canBulletGoThrow = false,
        canSmallBulletDestroy = true,
        amountElementsOnScreen = 1,
        width = BASE_WIDTH,
        height = BASE_HEIGHT,
        image = R.drawable.base
    ),

    PLAYER_VEH_SPAWN (
        canVehGoThrow = true,
        canBulletGoThrow = true,
        canSmallBulletDestroy = false,
        amountElementsOnScreen = 1,
        width = SPAWN_SIZE,
        height = SPAWN_SIZE,
        image = R.drawable.medium_tank
    ),

    ENEMY_VEH_SPAWN (
        canVehGoThrow = true,
        canBulletGoThrow = true,
        canSmallBulletDestroy = false,
        amountElementsOnScreen = 3,
        width = SPAWN_SIZE,
        height = SPAWN_SIZE,
        image = R.drawable.old_tank
    )

}