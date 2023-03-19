package com.resha.fless.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.resha.fless.model.AppPreference
import kotlinx.coroutines.launch

class OnboardingViewModel(private val appPref: AppPreference) : ViewModel() {
    fun editStatus(){
        viewModelScope.launch {
            appPref.editStatus()
        }
    }
}