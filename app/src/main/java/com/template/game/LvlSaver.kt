package com.template.game

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.template.game.models.Element

const val KEY_LVL = "key_lvl"

class LvlSaver(val context: Context) {
    private val prefs = (context as Activity).getPreferences(MODE_PRIVATE)

    fun loadLvl(): List<Element>? {
        val lvl = prefs.getString(KEY_LVL, null)
        var list: List<Element>? = null
        lvl?.let {
            val type = object :TypeToken<List<Element>>(){}.type
            list = Gson().fromJson(it, type)
        }
        return list
    }

    fun saveLvl(elements: List<Element>) {
        prefs.edit()
                .putString(KEY_LVL, Gson().toJson(elements))
                .apply()
    }
}