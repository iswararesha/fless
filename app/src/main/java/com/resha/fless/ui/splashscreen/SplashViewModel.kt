package com.resha.fless.ui.splashscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.resha.fless.model.AppPreference

class SplashViewModel(private val appPref: AppPreference) : ViewModel() {
    fun getStatus(): LiveData<Boolean> {
        return appPref.getStatus().asLiveData()
    }
}