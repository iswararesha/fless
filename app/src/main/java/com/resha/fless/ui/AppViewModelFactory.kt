package com.resha.fless.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.resha.fless.model.AppPreference
import com.resha.fless.model.UserPreference
import com.resha.fless.ui.onboarding.OnboardingViewModel
import com.resha.fless.ui.splashscreen.SplashViewModel

class AppViewModelFactory(private val appPref: AppPreference) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                SplashViewModel(appPref) as T
            }
            modelClass.isAssignableFrom(OnboardingViewModel::class.java) -> {
                OnboardingViewModel(appPref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}