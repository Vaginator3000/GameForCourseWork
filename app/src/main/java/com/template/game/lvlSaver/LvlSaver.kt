package com.template.game.lvlSaver

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.template.game.enums.Material
import com.template.game.models.Element

const val KEY_LVL = "key_lvl"

class LvlSaver(context: Context) {
    private val prefs = (context as Activity).getPreferences(MODE_PRIVATE)
    private val gson = Gson()

    fun loadLvl(): List<Element>? {
        val lvl = prefs.getString(KEY_LVL, null) ?: return null

        val type = object : TypeToken<List<Element>>() {}.type
        var loadedElements: List<Element> = gson.fromJson(lvl, type)

        val elementsWithNewIds = mutableListOf<Element>()

        loadedElements.forEach {
            elementsWithNewIds.add (
                        Element(
                                material = it.material,
                                coord = it.coord
                        )
                    )
        }
        return elementsWithNewIds
    }

    fun saveLvl(elements: List<Element>) {
        val elementsToSave = mutableListOf<Element>()
        elements.forEach {
            if (it.material != Material.PLAYER_VEH && it.material != Material.ENEMY_VEH)
                elementsToSave.add(it)
        }
        prefs.edit()
                .putString(KEY_LVL, gson.toJson(elementsToSave))
                .apply()
    }
}