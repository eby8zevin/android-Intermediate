package com.ahmadabuhasan.storyapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.ahmadabuhasan.storyapp.utils.Constant.KEY_IS_LOGIN
import com.ahmadabuhasan.storyapp.utils.Constant.KEY_TOKEN
import com.ahmadabuhasan.storyapp.utils.Constant.PREFS_NAME

class SessionManager(context: Context) {

    private var sharedPref: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val editor = sharedPref.edit()

    fun setBooleanPref(prefBoolean: String, value: Boolean) {
        editor.putBoolean(prefBoolean, value)
        editor.apply()
    }

    fun setStringPref(prefString: String, value: String) {
        editor.putString(prefString, value)
        editor.apply()
    }

    fun clearData() {
        editor.clear().apply()
    }

    val isLogin = sharedPref.getBoolean(KEY_IS_LOGIN, false)
    val getToken = sharedPref.getString(KEY_TOKEN, "")
}