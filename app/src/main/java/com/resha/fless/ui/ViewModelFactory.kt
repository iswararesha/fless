package com.resha.fless.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.resha.fless.ui.evaluation.EssayViewModel
import com.resha.fless.ui.evaluation.ObjectiveViewModel
import com.resha.fless.preference.UserPreference
import com.resha.fless.ui.course.CourseDetailViewModel
import com.resha.fless.ui.course.CourseViewModel
import com.resha.fless.ui.home.HomeViewModel
import com.resha.fless.ui.login.LoginViewModel
import com.resha.fless.ui.main.MainViewModel
import com.resha.fless.ui.material.MaterialViewModel
import com.resha.fless.ui.onboarding.OnboardingViewModel
import com.resha.fless.ui.profile.ProfileViewModel
import com.resha.fless.ui.register.RegisterViewModel
import com.resha.fless.ui.splashscreen.SplashViewModel
import com.resha.fless.ui.task.TaskViewModel

class ViewModelFactory (private val userPref: UserPreference) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                SplashViewModel(userPref) as T
            }
            modelClass.isAssignableFrom(OnboardingViewModel::class.java) -> {
                OnboardingViewModel(userPref) as T
            }
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
            modelClass.isAssignableFrom((CourseViewModel::class.java)) -> {
                CourseViewModel(userPref) as T
            }
            modelClass.isAssignableFrom((CourseDetailViewModel::class.java)) -> {
                CourseDetailViewModel(userPref) as T
            }
            modelClass.isAssignableFrom((MaterialViewModel::class.java)) -> {
                MaterialViewModel(userPref) as T
            }
            modelClass.isAssignableFrom((ObjectiveViewModel::class.java)) -> {
                ObjectiveViewModel(userPref) as T
            }
            modelClass.isAssignableFrom((EssayViewModel::class.java)) -> {
                EssayViewModel(userPref) as T
            }
            modelClass.isAssignableFrom((TaskViewModel::class.java)) -> {
                TaskViewModel(userPref) as T
            }
            modelClass.isAssignableFrom((HomeViewModel::class.java)) -> {
                HomeViewModel(userPref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}