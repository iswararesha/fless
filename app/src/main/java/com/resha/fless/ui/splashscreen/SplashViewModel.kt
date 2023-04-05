package com.resha.fless.ui.splashscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.resha.fless.model.UserPreference

class SplashViewModel(private val userPref: UserPreference) : ViewModel() {
    fun getStatus(): LiveData<Boolean> {
        return userPref.getStatus().asLiveData()
    }
}