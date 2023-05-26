package com.resha.fless.ui.splashscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.resha.fless.preference.UserPreference

class SplashViewModel(private val userPref: UserPreference) : ViewModel() {
    fun getStatus(): LiveData<Boolean> {
        return userPref.getStatus().asLiveData()
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return userPref.getThemeSetting().asLiveData()
    }
}