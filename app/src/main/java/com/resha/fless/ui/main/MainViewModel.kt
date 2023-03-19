package com.resha.fless.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.resha.fless.model.UserModel
import com.resha.fless.model.UserPreference

class MainViewModel (private val userPref: UserPreference) : ViewModel() {
    fun getUser(): LiveData<UserModel> {
        return userPref.getUser().asLiveData()
    }
}