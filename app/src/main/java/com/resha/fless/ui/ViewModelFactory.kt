package com.resha.fless.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.resha.fless.model.UserPreference
import com.resha.fless.ui.login.LoginViewModel
import com.resha.fless.ui.main.MainViewModel
import com.resha.fless.ui.profile.ProfileViewModel
import com.resha.fless.ui.register.RegisterViewModel

class ViewModelFactory (private val userPref: UserPreference) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userPref) as T
            }
            modelClass.isAssignableFrom((RegisterViewModel::class.java)) -> {
                RegisterViewModel(userPref) as T
            }
            modelClass.isAssignableFrom((LoginViewModel::class.java)) -> {
                LoginViewModel(userPref) as T
            }
            modelClass.isAssignableFrom((ProfileViewModel::class.java)) -> {
                ProfileViewModel(userPref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}