package com.resha.fless.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.resha.fless.preference.UserPreference
import kotlinx.coroutines.launch

class OnboardingViewModel(private val userPref: UserPreference) : ViewModel() {
    fun editStatus(){
        viewModelScope.launch {
            userPref.editStatus()
        }
    }
}