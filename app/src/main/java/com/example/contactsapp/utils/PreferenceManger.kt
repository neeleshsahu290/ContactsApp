package com.example.contactsapp.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManger (context:Context){
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constraints.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()


    fun putBoolean(Key: String, value: Boolean) {
        editor.putBoolean(Key, value)
        editor.apply()

    }

    fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun putString(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun clear() {
        editor.clear()
        editor.apply()
    }

}