package com.services.provider.data.prefs

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyPref @Inject constructor(private val preferences: SharedPreferences) {


    var keepMeSignedIn: Boolean
        get() = preferences.getBoolean("keepMeSignedIn", false)
        set(value) = preferences.edit().putBoolean("keepMeSignedIn", value).apply()


    var currentUser: String
        get() = preferences.getString("currentUser", "") ?: ""
        set(value) {
            preferences.edit().putString("currentUser", value).apply()
        }

}