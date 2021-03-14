package com.raj.weathertodo.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPref(val context: Context) {
    val TAG = "SharedPref"
    var sp: SharedPreferences;
    lateinit var editor: SharedPreferences.Editor

    init {
        sp = context.getSharedPreferences("myPef", Context.MODE_PRIVATE)
    }


    fun saveData(key: String, data: String) {
        editor = sp.edit()
        editor.putString(key, data)
        editor.commit()
    }


    fun getData(key: String): String {
        return sp.getString(key, "").toString()
    }

    fun removeData(key: String) {
        editor = sp.edit()
        editor.remove(key)
        editor.commit()
    }

}