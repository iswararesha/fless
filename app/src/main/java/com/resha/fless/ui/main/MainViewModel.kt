package com.resha.fless.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.resha.fless.preference.UserPreference


class MainViewModel (private val userPref: UserPreference) : ViewModel() {
    fun getThemeSettings(): LiveData<Boolean> {
        return userPref.getThemeSetting().asLiveData()
    }
}